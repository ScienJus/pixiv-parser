package bean;

import net.minidev.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 这是查询列表中的每个作品节点，它里面的属性可以用作筛选，但是它无法拿到大图链接
 * the list item in search/ranking/byAuthor, you can use the param to filter out it
 * @author ScienJus
 * @date 2015/8/11.
 */
public class IllustListItem {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String id;
    private AgeLimit ageLimit;
    private boolean manga;
    private int favoriteCount;
    private Timestamp createTime;

    public IllustListItem(JSONObject json) {
        this.id = json.getAsString("id");
        this.ageLimit = AgeLimit.customValueOf(json.getAsString("age_limit"));
        this.manga = Boolean.parseBoolean(json.getAsString("is_manga"));
        Number count = ((JSONObject)((JSONObject)json.get("stats")).get("favorited_count")).getAsNumber("public");
        this.favoriteCount = count == null ? 0 : count.intValue();
        try {
            this.createTime = new Timestamp(sdf.parse(json.getAsString("created_time")).getTime());
        } catch (ParseException e) {
            this.createTime = null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AgeLimit getAgeLimit() {
        return ageLimit;
    }

    public void setAgeLimit(AgeLimit ageLimit) {
        this.ageLimit = ageLimit;
    }

    public boolean isManga() {
        return manga;
    }

    public void setManga(boolean manga) {
        this.manga = manga;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "IllustListItem{" +
                "id='" + id + '\'' +
                ", ageLimit=" + ageLimit +
                ", manga=" + manga +
                ", favoriteCount=" + favoriteCount +
                ", createTime=" + createTime +
                '}';
    }
}
