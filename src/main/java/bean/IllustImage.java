package bean;

/**
 * @author XieEnlong
 * @date 2015/8/10.
 */
public class IllustImage {

    private String illustId;
    private String smallUrl;
    private String mediumUrl;
    private String largeUrl;
    private int serial;

    public String getIllustId() {
        return illustId;
    }

    public void setIllustId(String illustId) {
        this.illustId = illustId;
    }

    public String getSmallUrl() {
        return smallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        this.smallUrl = smallUrl;
    }

    public String getMediumUrl() {
        return mediumUrl;
    }

    public void setMediumUrl(String mediumUrl) {
        this.mediumUrl = mediumUrl;
    }

    public String getLargeUrl() {
        return largeUrl;
    }

    public void setLargeUrl(String largeUrl) {
        this.largeUrl = largeUrl;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getExtension() {
        if (largeUrl == null) {
            return null;
        }
        int extIndex = largeUrl.lastIndexOf(".");

        if (extIndex == -1) {
            return "";
        } else {
            return largeUrl.substring(extIndex + 1);
        }
    }

    @Override
    public String toString() {
        return "IllustImage{" +
                "illustId='" + illustId + '\'' +
                ", smallUrl='" + smallUrl + '\'' +
                ", mediumUrl='" + mediumUrl + '\'' +
                ", largeUrl='" + largeUrl + '\'' +
                ", serial=" + serial +
                '}';
    }
}
