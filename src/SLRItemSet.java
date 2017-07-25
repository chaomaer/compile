import java.util.*;

/**
 * 代表了一个项目集合
 */
public class SLRItemSet {
    private HashSet<SLRItem> items = new LinkedHashSet<>();
    private HashSet<String> nextstringset = new LinkedHashSet<>();
    private HashSet<String> visitnon = new LinkedHashSet<>();
    private Language language;
    private int statenum; // 为自己设定一个状态

    public void setStatenum(int statenum) {
        this.statenum = statenum;
    }

    public int getStatenum() {
        return statenum;
    }

    //判断两个集合是否相等,为了达到收敛
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SLRItemSet)) return false;
        boolean flag = false;
        SLRItemSet tmp = (SLRItemSet) obj;
        for (SLRItem item : items) {
            for (SLRItem slrItem : tmp.items) {
                if (item.equals(slrItem)){
                    flag = true;
                    break;
                }
            }
            if (flag){
                flag = false;
            }else {
                return false;
            }
        }
        return true;
    }

    //构造方法1
    public SLRItemSet(Language language){
         this.language = language;
    }
    //构造方法2,利用之前的项目集合和下一个记号进行
    public SLRItemSet(SLRItemSet itemSet, String nextstr){
        language = itemSet.language;
        for (SLRItem item : itemSet.items) {
            if (item.nextString!=null&&item.nextString.equals(nextstr)){
                add(new SLRItem(item));
            }
        }
    }
    public void add(SLRItem slrItem) {
         items.add(slrItem); // 把项目加入到集合中,同时把下一个记号加入nextstringset
         nextstringset.add(slrItem.nextString);
    }

    public void addtovisit(String s){
        visitnon.add(s);

    }

    public HashSet<SLRItem> getclosure() {
        String s;
        while ((s = IshavenonInItem()) != null){
            ArrayList<String> arrayList = language.nonterminalMap.get(s);
            for (String s1 : arrayList) {
                SLRItem temp = new SLRItem(s,s1);
                add(temp);
            }
            addtovisit(s);
        }
        return items;
    }

    private String IshavenonInItem() {
        for (String s : nextstringset) {
            if (language.nonterminalArraylist.contains(s)&&!visitnon.contains(s)){
                return s;
            }
        }
        return null;
    }
    public HashSet<String> getNextstringset(){
        getclosure();
        return nextstringset;
    }

    @Override
    public String toString() {
        return "这个项目集合的状态是"+statenum+"\n";
    }
}
