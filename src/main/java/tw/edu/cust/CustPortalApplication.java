package tw.edu.cust;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import tw.edu.cust.model.*;
import tw.edu.cust.repository.*;
import tw.edu.cust.service.NistAuditService;
import tw.edu.cust.service.ThreatDetectionService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class CustPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustPortalApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(
            UserRepository userRepository,
            NewsCategoryRepository categoryRepository,
            NewsRepository newsRepository,
            MailRepository mailRepository,
            NistAuditService auditService,
            ThreatDetectionService threatService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                // Initialize Default Users
                User admin = User.builder()
                        .username("admin")
                        .fullName("CUST 系統管理員")
                        .email("admin@cust.edu.tw")
                        .password(passwordEncoder.encode("CustAdmin2026!"))
                        .role(Role.ROLE_SUPER_ADMIN)
                        .department("圖書資訊中心")
                        .enabled(true)
                        .accountNonLocked(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                User faculty = User.builder()
                        .username("professor_chen")
                        .fullName("陳建宏 教授")
                        .email("chen.jh@cust.edu.tw")
                        .password(passwordEncoder.encode("ChenProf2026!"))
                        .role(Role.ROLE_FACULTY)
                        .department("航空機械系")
                        .enabled(true)
                        .accountNonLocked(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                User student = User.builder()
                        .username("student_lin")
                        .fullName("林冠宇 同學")
                        .email("lin.ky@cust.edu.tw")
                        .password(passwordEncoder.encode("LinStudent2026!"))
                        .role(Role.ROLE_STUDENT)
                        .department("資訊工程系")
                        .enabled(true)
                        .accountNonLocked(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

                userRepository.saveAll(List.of(admin, faculty, student));

                // Initialize News Categories
                NewsCategory catImportant = NewsCategory.builder().code("IMPORTANT").nameZh("重要公告").nameEn("Important Notices").badgeColor("#DC2626").displayOrder(1).build();
                NewsCategory catAcademic = NewsCategory.builder().code("ACADEMIC").nameZh("學術活動").nameEn("Academic Events").badgeColor("#2563EB").displayOrder(2).build();
                NewsCategory catAdmissions = NewsCategory.builder().code("ADMISSIONS").nameZh("招生訊息").nameEn("Admissions").badgeColor("#059669").displayOrder(3).build();
                NewsCategory catScholarship = NewsCategory.builder().code("SCHOLARSHIP").nameZh("獎助學金").nameEn("Scholarships").badgeColor("#D97706").displayOrder(4).build();
                NewsCategory catEvents = NewsCategory.builder().code("EVENTS").nameZh("校園活動").nameEn("Campus Events").badgeColor("#7C3AED").displayOrder(5).build();
                NewsCategory catResearch = NewsCategory.builder().code("RESEARCH").nameZh("研發產學").nameEn("Research & Industry").badgeColor("#0891B2").displayOrder(6).build();

                categoryRepository.saveAll(List.of(catImportant, catAcademic, catAdmissions, catScholarship, catEvents, catResearch));

                // Initialize CUST Realistic News
                News news1 = News.builder()
                        .title("【重要】中華科技大學通過教育部高等教育深耕計畫評鑑獲特優補助")
                        .content("本校榮獲教育部115年度「高等教育深耕計畫」績優肯定，獲核定全額獎助經費！本計畫將持續深化「智慧航空維修」、「健康科技照護」及「工程AI自動化」三大核心主軸，打造全國頂尖之技職教育標竿體系。")
                        .summary("本校榮獲教育部115年度高教深耕計畫評鑑特優肯定，全面推動智慧航空與AI創新教學。")
                        .category(catImportant)
                        .authorDepartment("校長室 / 研發處")
                        .authorName("秘書組")
                        .pinned(true)
                        .published(true)
                        .viewCount(1580)
                        .attachmentName("115年度深耕計畫成果報告.pdf")
                        .attachmentUrl("/assets/docs/hesp-report-2026.pdf")
                        .publishedAt(LocalDateTime.now().minusDays(1))
                        .createdAt(LocalDateTime.now().minusDays(1))
                        .build();

                News news2 = News.builder()
                        .title("【航空學院】中華科技大學航空維修教育中心通過EASA歐洲航空安全局認證評鑑")
                        .content("本校航空維修教育中心（CAS）再次順利通過歐盟EASA Part-147及民航局CAA雙重嚴格檢驗，具備發放國際通用民航維修工程師考照資格。中心配備先進B727實體客機、JT9D噴射引擎實驗室，培育國際一流飛航修護菁英。")
                        .summary("航空學院CAS中心通過歐洲航空安全局EASA最新認證，培育國際級航空修護專業人才。")
                        .category(catAcademic)
                        .authorDepartment("航空學院")
                        .authorName("CAS認證小組")
                        .pinned(true)
                        .published(true)
                        .viewCount(1240)
                        .attachmentName("EASA_Part147_Certificate.pdf")
                        .attachmentUrl("/assets/docs/easa-cert.pdf")
                        .publishedAt(LocalDateTime.now().minusDays(3))
                        .createdAt(LocalDateTime.now().minusDays(3))
                        .build();

                News news3 = News.builder()
                        .title("【招生專區】115學年度日間部四年制科技校院繁星計畫及甄選入學簡章公告")
                        .content("115學年度日間部四年制四技甄選入學、技優甄審及繁星推薦報名即將展開！設有智慧工程學院、健康科技學院、航空學院共12個熱門專業學系，提供高額新生入學獎學金及企業實習保障。")
                        .summary("115學年度日間部四年制甄選入學簡章正式發布，提供新生百萬獎助學金與就業保證。")
                        .category(catAdmissions)
                        .authorDepartment("教務處招生組")
                        .authorName("林主任")
                        .pinned(false)
                        .published(true)
                        .viewCount(890)
                        .publishedAt(LocalDateTime.now().minusDays(5))
                        .createdAt(LocalDateTime.now().minusDays(5))
                        .build();

                News news4 = News.builder()
                        .title("【獎學金】115學年度第一學期「中華科大卓越清寒暨優秀學生獎助學金」申請須知")
                        .content("為獎勵品學兼優及協助經濟不利學生安心就學，即日起開放申請校內外各項獎助學金。包含書卷獎、校友會紀念獎學金、生活助學金等多項補助，請於本月底前至生輔組填報。")
                        .summary("第一學期優秀學生及清寒獎助學金開放線上申請，請符合資格同學踴躍辦理。")
                        .category(catScholarship)
                        .authorDepartment("學生事務處 生輔組")
                        .authorName("王老師")
                        .pinned(false)
                        .published(true)
                        .viewCount(640)
                        .publishedAt(LocalDateTime.now().minusDays(6))
                        .createdAt(LocalDateTime.now().minusDays(6))
                        .build();

                News news5 = News.builder()
                        .title("【校園活動】2026 中華科大校慶運動大會暨無人機飛行競技表演賽")
                        .content("迎接58週年校慶！台北南港校區、新竹校區、雲林校區將同步舉辦系列慶祝活動。航空學院無人機戰隊將呈現極限飛行特技，現場更安排校園創意市集與產學成果博覽會，歡迎全體師生校友共襄盛舉！")
                        .summary("58週年校慶運動大會與無人機航空特技競技賽將於下週隆重登場。")
                        .category(catEvents)
                        .authorDepartment("課外活動指導組")
                        .authorName("活動策劃小組")
                        .pinned(false)
                        .published(true)
                        .viewCount(780)
                        .publishedAt(LocalDateTime.now().minusDays(7))
                        .createdAt(LocalDateTime.now().minusDays(7))
                        .build();

                newsRepository.saveAll(List.of(news1, news2, news3, news4, news5));

                // Initialize Realistic Mailbox
                MailMessage mail1 = MailMessage.builder()
                        .senderEmail("academic.affairs@cust.edu.tw")
                        .senderName("教務處 綜合教務組")
                        .recipientEmail("admin@cust.edu.tw")
                        .recipientName("系統管理員")
                        .subject("【教務通知】115學年度第一學期期中教學評量系統開放通知")
                        .body("各位同仁與同學好：\n\n115學年度第一學期教學意見調查系統已全面上線。請各院系所提醒同學登入系統完成填答，以作為未來課程精進之參考指標。\n\n系統已啟用 TLS 1.3 及 NIST SP 800-63B 強化認證機制，保障個資安全。\n\n教務處 敬啟")
                        .folder("INBOX")
                        .isRead(false)
                        .isStarred(true)
                        .hasAttachment(true)
                        .attachmentName("教學評量作業期程表.pdf")
                        .attachmentSize("1.4 MB")
                        .spfStatus("PASS")
                        .dkimStatus("PASS")
                        .dmarcStatus("PASS")
                        .tlsEncryption("TLSv1.3_ECDHE_RSA_AES256_GCM_SHA384")
                        .threatLevel("CLEAN")
                        .securityNotice("NIST SP 800-177 Verified: Authentic Sender & Encrypted Transit")
                        .receivedAt(LocalDateTime.now().minusHours(2))
                        .build();

                MailMessage mail2 = MailMessage.builder()
                        .senderEmail("security-center@cust.edu.tw")
                        .senderName("中華科技大學 資通安全中心")
                        .recipientEmail("admin@cust.edu.tw")
                        .recipientName("系統管理員")
                        .subject("【資安防護週報】NIST CSF 2.0 合規性檢查報告及威脅防禦狀態")
                        .body("資通安全監控中心（SOC）報告：\n\n本週校園骨幹網路防火牆累計攔截 1,420 次異常掃描試探。本校新一代入口網站系統運作正常，所有API端點均符合 NIST CSF 2.0 (Govern, Identify, Protect, Detect, Respond, Recover) 規範，無勒索軟體或未授權訪問異常。\n\n詳細合規矩陣如附件。")
                        .folder("INBOX")
                        .isRead(true)
                        .isStarred(true)
                        .hasAttachment(true)
                        .attachmentName("NIST_CSF_Weekly_Report_2026W33.pdf")
                        .attachmentSize("3.8 MB")
                        .spfStatus("PASS")
                        .dkimStatus("PASS")
                        .dmarcStatus("PASS")
                        .tlsEncryption("TLSv1.3_ECDHE_RSA_AES256_GCM_SHA384")
                        .threatLevel("CLEAN")
                        .securityNotice("NIST SP 800-177 Verified: SPF/DKIM Validated")
                        .receivedAt(LocalDateTime.now().minusHours(8))
                        .build();

                mailRepository.saveAll(List.of(mail1, mail2));

                // Initialize Audit Logs
                auditService.logEvent("SYSTEM_BOOT", "GOVERN", "SYSTEM", "127.0.0.1", "SpringBootEngine", "CUST Portal Core Services initialized successfully under NIST CSF 2.0", "INFO");
                auditService.logEvent("SECURITY_CONFIG_LOADED", "PROTECT", "SYSTEM", "127.0.0.1", "SecurityFilter", "Applied Strict-Transport-Security, CSP, and RBAC policies", "INFO");
                auditService.logEvent("SEED_DATA_INITIALIZED", "IDENTIFY", "admin", "127.0.0.1", "DataLoader", "Loaded initial academic departments, news categories and campus data", "INFO");

                // Initialize Security Incident sample
                threatService.reportIncident(
                        "外部異常探測掃描隔離", 
                        "邊界防火牆於 203.0.113.45 偵測到針對舊版 PHP 端點的自動化探測，已被 Nginx WAF 規則自動丟棄阻擋。", 
                        "SUSPICIOUS_IP", 
                        "LOW", 
                        "203.0.113.45", 
                        "PUBLIC_GATEWAY", 
                        "Auto-blacklisted IP for 24 hours at edge reverse proxy"
                );
            }
        };
    }
}
