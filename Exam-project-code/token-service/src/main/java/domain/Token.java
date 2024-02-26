package domain;

import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;
//import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;



/**
 *
 * @author Tamas
 */
//@XmlRootElement // Needed for XML serialization and deserialization
@Data // Automatic getter and setters and equals etc
@AllArgsConstructor
public class Token {
    public String userID;
    private String tokenID;

    public String getTokenID() {
        return tokenID;
    }
    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }


    public Token(String userID){
        this.userID = userID;
        this.tokenID = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token)) {
            return false;
        }
        var c = (Token) o;
        return tokenID != null && tokenID.equals(c.getTokenID()) ||
                tokenID == null && c.getTokenID() == null;
    }

    @Override
    public int hashCode() {
        return tokenID == null ? 0 : tokenID.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Token:  %s", tokenID);
    }
}
