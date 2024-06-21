package com.orfangenes.app.service.validator.FastaValidator;

public class FastaCallbackHandler implements FastaValidatorCallback {

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
}
