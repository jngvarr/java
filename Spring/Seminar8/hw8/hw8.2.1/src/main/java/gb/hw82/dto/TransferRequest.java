package gb.hw82.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {

  private long senderAccountId;
//  private long shopAccountId;
  private long amount;

}
