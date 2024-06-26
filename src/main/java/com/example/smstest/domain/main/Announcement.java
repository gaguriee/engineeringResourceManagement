package com.example.smstest.domain.main;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * 메인페이지 팝업 Entity
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "announcement")
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "is_display", nullable = false)
    private Boolean display;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "dismissd_users", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> dismissedUsers;
}
