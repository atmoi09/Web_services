package token.service;

import domain.Token;
import io.cucumber.java.an.E;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class TokenRepository {
    private Map<String, Token> tokenList;

    public TokenRepository() {
        this.tokenList = new HashMap<>();
    }

    /**
     *
     * @author Bence
     */
    public List<String> getTokenIdList(String customerId) throws Exception {
        if (customerId.length() == 0){
            throw new Exception("Customer id is empty");
        }
        List<String> tokens = new ArrayList<>();
        int requiredNumber = 0;
        Long numberOfTokens = tokenList.values().stream()
                .collect( groupingBy( Token::getUserID, Collectors.counting() ) ).get(customerId);
        if  (numberOfTokens == null) requiredNumber = 6;
        else requiredNumber = 6 - Math.toIntExact(numberOfTokens);
        if( numberOfTokens == null || numberOfTokens < 2) {
            for (int i = 0; i < requiredNumber; i++){
                Token token = new Token(customerId);
                tokenList.put( token.getTokenID(), token );
                tokens.add(token.getTokenID());
            }
            return tokens;
        }
        else {
            throw new Exception("Too many tokens");
        }
    }

    /**
     *
     * @author Tamas
     */
    public List<String> getArbitraryAmountOfTokenIdList(String customerId, int amount) throws Exception {
        if (customerId.length() == 0){
            throw new Exception("Customer id is empty");
        }
        List<String> tokens = new ArrayList<>();
        int requiredNumber = 0;
        Long numberOfTokens = tokenList.values().stream()
                .collect( groupingBy( Token::getUserID, Collectors.counting() ) ).get(customerId);
        if  (numberOfTokens == null) requiredNumber = 6;
        else requiredNumber = 6 - Math.toIntExact(numberOfTokens);
        requiredNumber = amount;
        if( numberOfTokens == null || numberOfTokens < 2) {
            for (int i = 0; i < requiredNumber; i++){
                Token token = new Token(customerId);
                tokenList.put( token.getTokenID(), token );
                tokens.add(token.getTokenID());
            }
            return tokens;
        }
        else {
            throw new Exception("Too many tokens");
        }
    }

    /**
     *
     * @author Tamas
     */
    public void deleteToken(String tokenID) throws Exception {
        if ( checkToken( tokenID ) ){
            tokenList.remove(tokenID);
        }else {
            throw new Exception ("Token not found");
        }
    }

    /**
     * @author Florian
     */
    public void deleteUserTokens(String userId){
        if (userId.length() == 0){
            System.out.println("userId is empty");
            return;
        }
        var tokens = tokenList.values().stream().filter(tk -> tk.getUserID().equals(userId)).collect(Collectors.toList());
        List<String> tokenIdKeys = new ArrayList<>();
        for (var token: tokens){
            tokenIdKeys.add(token.getTokenID());
        }
        if (tokens.isEmpty()){
            System.out.println("User: " + userId + " has no tokens");
        }
        else {
            for (var tokenId : tokenIdKeys) {
                tokenList.remove(tokenId);
            }
        }
    }

    /**
     *
     * @author Florian
     */
    public int getNumberOfTokensForUser(String userId){
        return tokenList.values().stream().filter(tk -> tk.getUserID().equals(userId)).collect(Collectors.toList()).size();
    }

    /**
     *
     * @author Tamas
     */
    public boolean checkToken(String providedTokenID) {
        return tokenList.containsKey(providedTokenID);
    }

    /**
     *
     * @author Bingkun
     */
    public String getCustomerIdByTokenId(String tokenId) throws Exception{
        if (tokenList.containsKey(tokenId)) {
            return tokenList.get(tokenId).getUserID();
        }
        else {
            throw new Exception("CustomerId Not Found");
        }
    }
}
