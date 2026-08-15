package tw.edu.cust.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.edu.cust.model.News;
import tw.edu.cust.model.NewsCategory;
import tw.edu.cust.repository.NewsCategoryRepository;
import tw.edu.cust.repository.NewsRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final NewsCategoryRepository categoryRepository;
    private final NistAuditService auditService;

    public List<NewsCategory> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public Page<News> getPublishedNews(int page, int size) {
        return newsRepository.findByPublishedTrueOrderByPinnedDescPublishedAtDesc(PageRequest.of(page, size));
    }

    public Page<News> getNewsByCategory(Long categoryId, int page, int size) {
        return newsRepository.findByPublishedTrueAndCategoryIdOrderByPinnedDescPublishedAtDesc(categoryId, PageRequest.of(page, size));
    }

    public Page<News> searchNews(String query, int page, int size) {
        return newsRepository.searchNews(query, PageRequest.of(page, size));
    }

    public List<News> getFeaturedNews() {
        return newsRepository.findTop5ByPublishedTrueOrderByPinnedDescPublishedAtDesc();
    }

    @Transactional
    public Optional<News> getNewsByIdAndIncrementView(Long id) {
        return newsRepository.findById(id).map(news -> {
            news.setViewCount(news.getViewCount() + 1);
            return newsRepository.save(news);
        });
    }

    @Transactional
    public News createNews(String title, String content, String summary, Long categoryId, String authorDept, String authorName, boolean pinned, String attachmentName, String attachmentUrl, String clientIp, String username) {
        NewsCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID: " + categoryId));

        News news = News.builder()
                .title(title)
                .content(content)
                .summary(summary != null && !summary.isBlank() ? summary : (content.length() > 150 ? content.substring(0, 150) + "..." : content))
                .category(category)
                .authorDepartment(authorDept)
                .authorName(authorName)
                .pinned(pinned)
                .published(true)
                .viewCount(0)
                .attachmentName(attachmentName)
                .attachmentUrl(attachmentUrl)
                .publishedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        News saved = newsRepository.save(news);
        auditService.logEvent("NEWS_CREATED", "PROTECT", username, clientIp, null, 
                "Created news ID=" + saved.getId() + " title='" + saved.getTitle() + "' in category " + category.getNameZh(), "INFO");
        return saved;
    }

    @Transactional
    public News updateNews(Long id, String title, String content, String summary, Long categoryId, boolean pinned, boolean published, String clientIp, String username) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("News not found: " + id));

        NewsCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Category ID: " + categoryId));

        news.setTitle(title);
        news.setContent(content);
        news.setSummary(summary);
        news.setCategory(category);
        news.setPinned(pinned);
        news.setPublished(published);
        news.setUpdatedAt(LocalDateTime.now());

        News updated = newsRepository.save(news);
        auditService.logEvent("NEWS_UPDATED", "PROTECT", username, clientIp, null, 
                "Updated news ID=" + updated.getId() + " title='" + updated.getTitle() + "'", "INFO");
        return updated;
    }

    @Transactional
    public void deleteNews(Long id, String clientIp, String username) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("News not found: " + id));
        newsRepository.delete(news);
        auditService.logEvent("NEWS_DELETED", "PROTECT", username, clientIp, null, 
                "Deleted news ID=" + id + " title='" + news.getTitle() + "'", "WARN");
    }
}
