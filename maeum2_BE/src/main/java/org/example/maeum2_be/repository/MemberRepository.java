package org.example.maeum2_be.repository;

import org.example.maeum2_be.entity.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    public Member findByMemberId(String memberId);
}
