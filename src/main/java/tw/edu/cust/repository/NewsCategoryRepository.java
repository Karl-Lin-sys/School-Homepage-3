package tw.edu.cust.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.edu.cust.model.NewsCategory;
import java.util.Optional;
import java.util.List;

@Repository
public interface NewsCategoryRepository extends JpaRepository<NewsCategory, Long> {
    Optional<NewsCategory> findByCode(String code);
    List<NewsCategory> findAllByOrderByDisplayOrderAsc();
}
