import java.math.BigInteger;

public class CompteBancari {

    private String IBAN;

    public CompteBancari(String string) {
    }
    public void IBAN(String IBAN) {
        if (validarIBAN(IBAN)) {
            this.IBAN = IBAN;
        }
    }
    public static boolean validarIBAN(String conta){
        boolean esValit = false;
        int i = 2;
        int caracterASCII = 0;
        int resto = 0;
        int dc = 0;
        String cadenaDc = "";
        BigInteger contaNumero = new BigInteger("0");
        BigInteger mode = new BigInteger("97");

        if(conta.length() == 24 && conta.substring(0,1).toUpperCase().equals("E")
            && conta.substring(1,2).toUpperCase().equals("S")){
                do{
                    caracterASCII = conta.codePointAt(i);
                    esValit = (caracterASCII > 47 && caracterASCII < 58);
                    i++;
                } while(i < conta.length() && esValit);

                if(esValit) {
                    contaNumero = new BigInteger(conta.substring(4, 24) + "142800");
                    resto = contaNumero.mod(mode).intValue();
                    dc = 98 - resto;
                    cadenaDc = String.valueOf(dc);
                }

                if(dc < 10){
                    cadenaDc = "0" + cadenaDc;
                }

                if(conta.substring(2,4).equals(cadenaDc)) {
                    esValit = true;
                } else {
                    esValit = false;
                }
            }

            return esValit;
    }

    public boolean setIBAN(String next) {
        return false;
    }

    public void setIBAN() {

    }
      


}
