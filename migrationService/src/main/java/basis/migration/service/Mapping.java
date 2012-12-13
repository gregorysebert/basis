package basis.migration.service;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: gregorysebert
 * Date: 06/12/12
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class Mapping {
    private String DOSNUM  = "";
    private String SYSDAT = "";
    private String DOCNUM = "";
    private String OPSTELER = "";
    private String AFSENDER = "";
    private String ZAAK= "";
    private String KLASSEMENT = "";
    private String BESTEMMELING = "";
    private String STAMNUMMER = "";

    private enum DB {
        PERS, GBBT;
    }

    public Mapping(String database,HashMap<String, String> docMap )
    {
        DB db = DB.valueOf(database);

        switch(db) {
            case PERS:
                loadPERS(docMap);
                break;
            case GBBT:
                loadGBBT(docMap);
                break;
        }
    }

    void loadPERS(HashMap<String, String> docMap) {
      String [] s = docMap.get("DOCIDT").split("\\.");
      this.DOSNUM = s[0];
      this.SYSDAT = docMap.get("DOCDAT");
      this.DOCNUM = s[1];
      this.OPSTELER = docMap.get("ADMINT");
      this.AFSENDER = docMap.get("ARTNUM");
      this.ZAAK = docMap.get("BULNUM");
      this.KLASSEMENT = docMap.get("CLSNUM");
      this.BESTEMMELING = docMap.get("DOCORG");
      this.STAMNUMMER = docMap.get("STAMNUMMER");
    }

    void loadGBBT(HashMap<String, String> docMap) {
      this.DOSNUM = docMap.get("DOSNUM");
      this.SYSDAT = docMap.get("SYSDAT");
      this.DOCNUM = docMap.get("DOCNUM");
    }



    public String getDOSNUM() {
        return DOSNUM;
    }

    public String getSYSDAT() {
        return SYSDAT;
    }

    public String getDOCNUM() {
        return DOCNUM;
    }


    public String getOPSTELER() {
        return OPSTELER;
    }

    public String getAFSENDER() {
        return AFSENDER;
    }

    public String getZAAK() {
        return ZAAK;
    }

    public String getKLASSEMENT() {
        return KLASSEMENT;
    }

    public String getBESTEMMELING() {
        return BESTEMMELING;
    }

    public String getSTAMNUMMER() {
        return STAMNUMMER;
    }

}
