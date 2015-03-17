import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责提取html内容的类
 */
public class PageParser {
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(PageParser.class);

    /**
     * 主机地址
     */
    private static final String host = "http://www.pixiv.net";

    /**
     * 在搜索列表中过滤出制定条件的图片
     * @param pageHtml
     * @param praise
     * @return
     */
    public List<String> parseList(String pageHtml, int praise) {
        try {
            List<String> items = new ArrayList<String>();
            Parser parser = new Parser(pageHtml);
            NodeFilter filter = new AndFilter(new TagNameFilter("li"),new HasAttributeFilter("class","image-item"));
            NodeList list = parser.parse(filter);
            for (int i = 0; i < list.size(); i++) {
                Node item = list.elementAt(i);
                NodeList childs = item.getChildren();
                String uri = ((LinkTag)childs.elementAt(0)).getLink();
                String count = "0";
                if (childs.size() == 6) {
                    count = childs.elementAt(5).getFirstChild().getFirstChild().getLastChild().getText();
                }
                if (Integer.parseInt(count) > praise) {
                    items.add(host + uri);
                }
            }
            return items;
        } catch (ParserException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 在搜索列表中找到下一页的地址
     * @param pageHtml
     * @return
     */
    public String parseNextPage(String pageHtml) {
        try {
            Parser parser = new Parser(pageHtml);
            NodeFilter filter = new AndFilter(new TagNameFilter("a"),new HasAttributeFilter("rel","next"));
            NodeList list =  parser.parse(filter);
            if(list.size() > 0) {
                return ((LinkTag)list.elementAt(0)).getLink();
            }
        } catch (ParserException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 判断该页面是否有多张图片
     * @param pageHtml
     * @return
     */
    public boolean isManga(String pageHtml) {
        if (pageHtml.indexOf("一次性投稿多张作品") > -1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 提取单张图片
     * @param pageHtml
     * @return
     */
    public String parseMedium(String pageHtml) {
        try {
            Parser parser = new Parser(pageHtml);
            NodeFilter filter = new AndFilter(new TagNameFilter("img"),new HasAttributeFilter("class","original-image"));
            NodeList list = parser.parse(filter);
            if (list.size() > 0) {
                return ((ImageTag)list.elementAt(0)).getAttribute("data-src");
            }
        } catch (ParserException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    /**
     * 提取多张图片
     * @param pageHtml
     * @return
     */
    public List<String> parseManga(String pageHtml) {
        try {
            List<String> result = new ArrayList<String>();
            Parser parser = new Parser(pageHtml);
            NodeFilter filter = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class","item-container"));
            NodeList list = parser.parse(filter);
            for (int i = 0; i < list.size(); i++) {
                Node item = list.elementAt(i);
                result.add(((ImageTag) item.getChildren().elementAt(2)).getAttribute("data-src"));
            }
            return result;
        } catch (ParserException e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
