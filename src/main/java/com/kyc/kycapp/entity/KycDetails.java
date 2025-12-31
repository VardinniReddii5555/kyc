package com.kyc.kycapp.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kyc_details")
public class KycDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "pan_number", nullable = false, unique = true)
    private String panNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "video_path", nullable = false)
    private String videoPath;

    // getters & setters
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getVideoPath() { return videoPath; }
    public void setVideoPath(String videoPath) { this.videoPath = videoPath; }
}
