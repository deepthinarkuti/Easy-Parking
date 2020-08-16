package siokouros.filippos.phonepark.Model;

import java.util.Date;

public class Payment {
    private String cardType;
    private String number;
    private int CVC;
    private Date expDate;

    public Payment() {
    }

    public Payment(String cardType, String number, int CVC, Date expDate) {
        this.cardType = cardType;
        this.number = number;
        this.CVC = CVC;
        this.expDate = expDate;
    }



    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCVC() {
        return CVC;
    }

    public void setCVC(int CVC) {
        this.CVC = CVC;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }
}
