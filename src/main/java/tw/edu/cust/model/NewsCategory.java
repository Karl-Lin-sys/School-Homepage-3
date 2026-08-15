package tw.edu.cust.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "news_categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String code; // e.g., 'IMPORTANT', 'ACADEMIC', 'ADMISSIONS', 'SCHOLARSHIP', 'EVENTS', 'RESEARCH'

    @Column(nullable = false, length = 100)
    private String nameZh; // e.g., '重要公告'

    @Column(nullable = false, length = 100)
    private String nameEn; // e.g., 'Important Notices'

    private String badgeColor; // CSS hex color code
    private int displayOrder;
}
