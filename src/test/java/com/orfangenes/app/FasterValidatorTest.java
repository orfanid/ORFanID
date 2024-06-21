package com.orfangenes.app;

import com.orfangenes.app.service.validator.FastaValidator.FastaHandlingException;
import com.orfangenes.app.service.validator.FastaValidator.FastaValidator;
import com.orfangenes.app.service.validator.FastaValidator.FastaValidatorCallback;
import com.orfangenes.app.service.validator.FastaValidator.FastaValidatorException;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;

@Disabled
public class FasterValidatorTest implements FastaValidatorCallback {

    public void header(String header)
    {
        System.out.println(header);
    }

    public void commentline(String commentline)
    {
        System.out.println(commentline);
    }

    public void seqline(String seqline)
    {
        System.out.println(seqline);
    }

    public void eof()
    {
        System.out.println("end of file");
    }

    @Test
    public void validate() throws FastaHandlingException, FastaValidatorException, IOException {
        FastaValidator FV = new FastaValidator(new FasterValidatorTest());
        FV.setSequencetype(FastaValidator.Sequencetype.PROTEIN);

        FV.validate("FYWISKFHENAAEGNTFNATYFPHLGLDIHYQIRWTMFCWCSTLCRTTGQVSDIYYQVEE\n" +
                "YYKRCPWHLFGCQVAWFPYYWQFIAEQWNFYVEHDSHWENNILEEHFFNRWCKHKSWKQE\n" +
                "WPFGFPCASPTTTLSRWIMFGAKDTHYDCVTAAEGGCQTKVHQGDRRIIPFKEDPHMFLL\n" +
                "AYDNFQLLFMAKEPDEKCTKQEQMSHLGRMDNYSHMLNCYWQATRIQIPNAAGVGEILAG\n" +
                "YAIYWEFYTTYKKAGIIEGHYQFWINVCVRVFVQDRTVHNILYINTMTFAILMCCLGMWL\n" +
                "VKRFGATAILFIFKYTTRPKNLRMSWLFYQKDHAYCKNSPDQQRFNFGNQYSEPPNSLHF\n" +
                "WMQTYCRMIWWKQELKSYPQLYDDGAQIKWIKLSAMSGNPTSLSHYKGEKRRIHVPQSHI\n" +
                "RNLFMGEGHFSAKTPEFLTSFHPRKHGPDMRSERNGRFSMMCHVWFHYTRGPLIVNSWFH\n" +
                "TFKYWGVHDKCAFQPLMFQVLEGAQCGTLQSRRFFFVKARGGGTPGMQQLNRMRHPRTDM\n" +
                "FAHFMGAKEHFWQAHRRVHAYDSICHKERQIESLWPRIDSRGYNKQKLPFASEYSMAACG\n" +
                "SHTRKKWTFWVAVQRYLAFIKRGVGMVLCYRGFYSVNNHTHFGTWSESLVAETDFKGIVH\n" +
                "VEESWFNPMNVEVYPLERNRADVQQPSCTVTDMQHTVRDQHMSDGETVWYFTRSRHESNP\n" +
                "YFDSMKQCSWFKHDAIPNYYLVQKDDGPVQWKYHYPPQQIGGTWITKTPNTPLILQCSGT\n" +
                "LIRVSVWMWQWMLVSTTNKYQQSDDMFTHIHIVGRWPRTTIFMTDDDTFVPPITYLFMAP\n" +
                "FVPHKDGHQVIFVVPKKGLHCQNEHANCKEARTVANAVYGQQGKTWLQIVAKSRREAPAI\n" +
                "QGEEYTTVSLQENDSMVEPGGLVPINCIRNMTRRKVWDWHLRCWETPCMWRNDVEWYHVC\n" +
                "TPYRNIRARWPPVRQCESICDHRQCNAMYRLCTYMQTEHC");
    }
}
