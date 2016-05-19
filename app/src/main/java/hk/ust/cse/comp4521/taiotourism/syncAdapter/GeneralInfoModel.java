package hk.ust.cse.comp4521.taiotourism.syncAdapter;

import com.strongloop.android.loopback.Model;

/**
 * Created by ethanraymond on 5/19/16.
 */
public class GeneralInfoModel extends Model {

    //private String id;
    private String ywcaDesciption;
    private String ywcaDesciptionCH;
    private String taiODescription;
    private String taiODescriptionCH;

    GeneralInfoModel() {};

    public String getYwcaDesciption() {
        return ywcaDesciption;
    }

    public void setYwcaDesciption(String ywcaDesciption) {
        this.ywcaDesciption = ywcaDesciption;
    }

    public String getYwcaDesciptionCH() {
        return ywcaDesciptionCH;
    }

    public void setYwcaDesciptionCH(String ywcaDesciptionCH) {
        this.ywcaDesciptionCH = ywcaDesciptionCH;
    }

    public String getTaiODescription() {
        return taiODescription;
    }

    public void setTaiODescription(String taiODescription) {
        this.taiODescription = taiODescription;
    }

    public String getTaiODescriptionCH() {
        return taiODescriptionCH;
    }

    public void setTaiODescriptionCH(String taiODescriptionCH) {
        this.taiODescriptionCH = taiODescriptionCH;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
}
