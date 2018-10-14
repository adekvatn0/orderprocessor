package io.ambershogun.request.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BuyRequest extends Request {

    @Min(0)
    private long buyAmount;

    @NotNull
    @Size(max = 128)
    private String stockName;

    public BuyRequest(long buyAmount, String stockName) {
        this.buyAmount = buyAmount;
        this.stockName = stockName;
    }
}
