package com.bazuuyu.b2b.wholesale.dto.request;

import com.bazuuyu.b2b.wholesale.entity.enums.ApplicationReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewWholesaleApplicationRequest {

    @NotNull
    private ApplicationReviewStatus decision;

    @Size(max = 1000)
    private String reviewNote;

    public ApplicationReviewStatus getDecision() {
        return decision;
    }

    public void setDecision(ApplicationReviewStatus decision) {
        this.decision = decision;
    }

    public String getReviewNote() {
        return reviewNote;
    }

    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }
}
