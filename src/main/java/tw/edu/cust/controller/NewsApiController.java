package tw.edu.cust.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tw.edu.cust.model.News;
import tw.edu.cust.model.NewsCategory;
import tw.edu.cust.service.NewsService;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class NewsApiController {

    private final NewsService newsService;

    @GetMapping("/categories")
    public ResponseEntity<List<NewsCategory>> getAllCategories() {
        return ResponseEntity.ok(newsService.getAllCategories());
    }

    @GetMapping("/news")
    public ResponseEntity<Page<News>> getNews(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(newsService.searchNews(search.trim(), page, size));
        }
        if (categoryId != null && categoryId > 0) {
            return ResponseEntity.ok(newsService.getNewsByCategory(categoryId, page, size));
        }
        return ResponseEntity.ok(newsService.getPublishedNews(page, size));
    }

    @GetMapping("/news/featured")
    public ResponseEntity<List<News>> getFeaturedNews() {
        return ResponseEntity.ok(newsService.getFeaturedNews());
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<News> getNewsDetail(@PathVariable Long id) {
        return newsService.getNewsByIdAndIncrementView(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/news")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'FACULTY')")
    public ResponseEntity<News> createNews(@RequestBody CreateNewsRequest req, HttpServletRequest request, Authentication auth) {
        String clientIp = request.getRemoteAddr();
        String username = auth != null ? auth.getName() : "ADMIN";

        News created = newsService.createNews(
                req.getTitle(),
                req.getContent(),
                req.getSummary(),
                req.getCategoryId(),
                req.getAuthorDepartment() != null ? req.getAuthorDepartment() : "中華科技大學 秘書室",
                req.getAuthorName() != null ? req.getAuthorName() : username,
                req.isPinned(),
                req.getAttachmentName(),
                req.getAttachmentUrl(),
                clientIp,
                username
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/news/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody CreateNewsRequest req, HttpServletRequest request, Authentication auth) {
        String clientIp = request.getRemoteAddr();
        String username = auth != null ? auth.getName() : "ADMIN";

        News updated = newsService.updateNews(
                id,
                req.getTitle(),
                req.getContent(),
                req.getSummary(),
                req.getCategoryId(),
                req.isPinned(),
                req.isPublished(),
                clientIp,
                username
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/news/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteNews(@PathVariable Long id, HttpServletRequest request, Authentication auth) {
        String clientIp = request.getRemoteAddr();
        String username = auth != null ? auth.getName() : "ADMIN";
        newsService.deleteNews(id, clientIp, username);
        return ResponseEntity.noContent().build();
    }

    @Data
    public static class CreateNewsRequest {
        private String title;
        private String content;
        private String summary;
        private Long categoryId;
        private String authorDepartment;
        private String authorName;
        private boolean pinned;
        private boolean published = true;
        private String attachmentName;
        private String attachmentUrl;
    }
}
