package com.datou.author.service;

import static com.google.common.collect.Collections2.transform;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.stereotype.Component;

import com.datou.author.model.MongoApproval;
import com.datou.author.repositories.MongoApprovalRepository;
import com.google.common.base.Function;

@Component
public class MongoApprovalStore implements ApprovalStore {
	@Autowired
    private MongoApprovalRepository mongoApprovalRepository;

    private boolean handleRevocationsAsExpiry = false;

    @Override
    public boolean addApprovals( Collection<Approval> approvals) {
         Collection<MongoApproval> mongoApprovals = transform(approvals, toMongoApproval());

        return mongoApprovalRepository.updateOrCreate(mongoApprovals);
    }

    @Override
    public boolean revokeApprovals( Collection<Approval> approvals) {
        boolean success = true;
        Collection<MongoApproval> mongoApprovals = transform(approvals, toMongoApproval());
        for (MongoApproval mongoApproval : mongoApprovals) {
            if (handleRevocationsAsExpiry) {
                boolean updateResult = mongoApprovalRepository.updateExpiresAt(LocalDate.now(), mongoApproval);
                if (!updateResult) {
                    success = false;
                }
            }else {
                 boolean deleteResult = mongoApprovalRepository.deleteByUserIdAndClientIdAndScope(mongoApproval);
                if (!deleteResult) {
                    success = false;
                }
            }
        }
        return success;
    }

    @Override
    public Collection<Approval> getApprovals(String userId,
                                              String clientId) {
         List<MongoApproval> mongoApprovals = mongoApprovalRepository.findByUserIdAndClientId(userId, clientId);
        return transform(mongoApprovals, toApproval());
    }

    private Function<Approval, MongoApproval> toMongoApproval() {
        return new Function<Approval, MongoApproval>() {
            @Override
            public MongoApproval apply(Approval approval) {
                return new MongoApproval(UUID.randomUUID().toString(),
                        approval.getUserId(),
                        approval.getClientId(),
                        approval.getScope(),
                        approval.getStatus() == null ? Approval.ApprovalStatus.APPROVED: approval.getStatus(),
                        LocalDate.fromDateFields(approval.getExpiresAt()),
                        LocalDate.fromDateFields(approval.getLastUpdatedAt()));
            }
        };
    }

    private Function<MongoApproval, Approval> toApproval() {
        return new Function<MongoApproval, Approval>() {
            @Override
            public Approval apply(MongoApproval mongoApproval) {
                return new Approval(mongoApproval.getUserId(),
                        mongoApproval.getClientId(),
                        mongoApproval.getScope(),
                        mongoApproval.getExpiresAt().toDate(),
                        mongoApproval.getStatus(),
                        mongoApproval.getLastUpdatedAt().toDate());
            }
        };
    }

    public void setHandleRevocationsAsExpiry(boolean handleRevocationsAsExpiry) {
        this.handleRevocationsAsExpiry = handleRevocationsAsExpiry;
    }
}
