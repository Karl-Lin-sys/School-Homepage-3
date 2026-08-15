package tw.edu.cust.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.edu.cust.model.News;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByPublishedTrueOrderByPinnedDescPublishedAtDesc(Pageable pageable);
    
    Page<News> findByPublishedTrueAndCategoryIdOrderByPinnedDescPublishedAtDesc(Long categoryId, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.published = true AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(n.authorDepartment) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY n.pinned DESC, n.publishedAt DESC")
    Page<News> searchNews(@Param("query") String query, Pageable pageable);

    List<News> findTop5ByPublishedTrueOrderByPinnedDescPublishedAtDesc();
}
