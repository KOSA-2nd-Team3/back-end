package kosa.server.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_id", unique=true, nullable = false)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private int capacity;

    @Column(nullable=false)
    private BigDecimal price;

    @Column(nullable=false)
    private int category;

    @Column(nullable = false)
    private int monthUnit;

    @Column(name = "image_url", nullable=false)
    private String imageUrl;

    @OneToMany(mappedBy = "platform")
    private List<Post> post = new ArrayList<>();
}