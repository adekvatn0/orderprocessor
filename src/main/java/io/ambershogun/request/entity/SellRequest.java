package io.ambershogun.request.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.Min;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SellRequest extends Request {

    @Min(0)
    private long sellAmount;

    public SellRequest(long sellAmount) {
        this.sellAmount = sellAmount;
    }
}
