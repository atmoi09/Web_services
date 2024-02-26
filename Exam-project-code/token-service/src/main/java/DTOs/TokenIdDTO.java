package DTOs;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Bence
 */
@Data
public class TokenIdDTO implements Serializable {
    private static final long serialVersionUID = -7170063595574709456L;
    List<String> tokenIdList;
}
