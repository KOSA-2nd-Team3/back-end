package kosa.server.board.entity;

import jakarta.persistence.*;
import kosa.server.board.dto.request.PostUpdateRequestDto;
import kosa.server.common.BaseEntity;
import kosa.server.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @Column(unique = true, nullable=false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    private Platform platform;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post")
    private List<PartyMember> partyMember = new ArrayList<>();

    @Setter
    @Column(name = "current_count", nullable=false)
    private int currentCount;

    @Column(name = "party_size", nullable=false)
    private int partySize;

    @Column(name = "duration_month" , nullable=false)
    private int durationMonth;

    @Column(name = "host_id")
    private String hostId;

    @Column(name = "host_pwd")
    private String hostPwd;

    @Column(name = "is_expired", nullable = false)
    private String isExpired;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    public void addPartyMember(PartyMember partyMember) {
        this.partyMember.add(partyMember);
    }

    @Builder
    public Post(Platform platform, Member member, int current_count, int partySize, int durationMonth , String hostId, String hostPwd, String isExpired) {
        this.platform = platform;
        this.member = member;
        this.currentCount = current_count;
        this.partySize = partySize;
        this.durationMonth = durationMonth;
        this.hostId = hostId;
        this.hostPwd = hostPwd;
        this.isExpired = isExpired;
    }

    public PostUpdateRequestDto.PostUpdateRequestDtoBuilder toEditor() {
        return PostUpdateRequestDto.builder()
                .durationMonth(this.durationMonth)
                .hostId(this.hostId)
                .hostPwd(this.hostPwd);
    }

    public void edit(PostUpdateRequestDto dto) {
        this.durationMonth = dto.getDurationMonth();
        this.hostId = dto.getHostId();
        this.hostPwd = dto.getHostPwd();
    }

    public void expired() {
        this.isExpired = "Y";
    }

    public void startService(int durationMonth){
        this.startDate = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusMonths(durationMonth);
    }
}
