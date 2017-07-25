import java.util.*;

public class Language {
    public ArrayList<String> terminalArraylist = new ArrayList<>();
    public HashMap<String,ArrayList<String>>  nonterminalMap =  new HashMap<>();
    public ArrayList<String> nonterminalArraylist = new ArrayList<>();
    public String startsymbol;

    public void setTerminalArraylist(ArrayList<String> terminalArraylist){
        this.terminalArraylist = terminalArraylist;
    }
    private String Isstartwithnon(String s){
        for (String s1 : nonterminalArraylist) {
            if (s.startsWith(s1)){
                return s1;
            }
        }
        return null;
    }
    private String Isstartwithter(String s){
        for (String s1 : terminalArraylist) {
            if (s.startsWith(s1)){
                return s1;
            }
        }
        return null;
    }
    private ArrayList<String> dealwithFirstNon(String key,String s){
        ArrayList<String> arrayList = new ArrayList<>();
        // 为递归回来使用，首先检查是否是终结符，如果是:
        String ter = Isstartwithter(s);
        if (ter != null){
            arrayList.add(ter);
        }else {
            // 首先得到s开始得到非终结符
            String non = Isstartwithnon(s);
            assert non != null;
            if (non.equals(key)) return arrayList;
            // 检查s的first集合中是否有&(代表空)
            ArrayList<String> firstset = First(non);
            if (firstset.contains("&")){
                // 如果有空,加入除&的所以元素
                // 然后调用剩下的元素
                // 检查是否有剩余的元素,如果没有，直接返回
                if (non.length() == s.length()){
                    arrayList.addAll(firstset);
                }else {
                    firstset.remove("&");
                    arrayList.addAll(firstset);
                    arrayList.addAll(dealwithFirstNon(key,s.substring(non.length(),s.length())));
                }
            }else {
                // 如果没有空，直接全部加入，然后终结调用过程
                arrayList.addAll(firstset);
            }
        }
        return arrayList;
    }

    public Language(String ss, ArrayList<String> arrayList,String startsymbol){
        fillmap(ss);
        setTerminalArraylist(arrayList);
        this.startsymbol = startsymbol;
    }
    public void testterminal(){
        for (String s : terminalArraylist) {
            System.out.print(s+"  ");
        }
        System.out.println();
    }

    public void testmap() {
        for (String s : nonterminalMap.keySet()) {
            System.out.print(s+"->");
            boolean first = true;
            for (String ss : nonterminalMap.get(s)) {
                if (first){
                    System.out.print(ss);
                    first = false;
                }else {
                    System.out.print("|"+ss);
                }
            }
            System.out.println();
        }
    }

    private void fillmap(String ss) {
        String [] buffer = ss.split("\n");
        for (String s : buffer) {
            int index = s.indexOf("->");
            String var1 = s.substring(0,index);
            String var2 = s.substring(index+2,s.length());
            String[] temps = var2.split("\\|");
            ArrayList<String> arrayList = new ArrayList<>();
            for (String temp : temps) {
                arrayList.add(temp.trim());
            }
            nonterminalMap.put(var1,arrayList);
        }
        nonterminalArraylist.addAll(nonterminalMap.keySet());
    }
    public ArrayList<String> First(String nonterminal){
        ArrayList<String> retlist = new ArrayList<>();
        ArrayList<String> temps = nonterminalMap.get(nonterminal);
        for (String temp : temps) {
            String ter;
            if (( ter = Isstartwithter(temp))!=null){
                retlist.add(ter);
            }else {
                retlist.addAll(dealwithFirstNon(nonterminal,temp));
            }
        }
        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(retlist);
        retlist.clear();
        retlist.addAll(hashSet);
        return retlist;
    }
    public void testFirst(){
        for (String s : nonterminalArraylist) {
            ArrayList<String> arrayList = First(s);
            System.out.println(s+"的first集合是:");
            for (String s1 : arrayList) {
                System.out.print(s1+"  ");
            }
            System.out.println();
        }
    }

    public void testFollow(){
        for (String s : nonterminalArraylist) {
            ArrayList<String> arrayList = Follow(s);
            System.out.println(s+"的follow集合是:");
            for (String s1 : arrayList) {
                System.out.print(s1+"  ");
            }
            System.out.println();
        }
    }
    private ArrayList<String> dealwithFollowNon(String key,String s){
        ArrayList<String> retlist = new ArrayList<>();
        //s肯定是有内容的
        //如果s的开始符号的终结符，直接添加并返回
        String ter;
        if ((ter = Isstartwithter(s))!=null){
            retlist.add(ter);
        }else {
            String non = Isstartwithnon(s);
            ArrayList<String> arr = First(non);
            assert non != null;
            if (non.length() == s.length()){
                // 说明是最后一个
                if (arr.contains("&")){
                    arr.remove("&");
                    retlist.addAll(arr);
                    retlist.add(key);
                }else {
                    retlist.addAll(arr);
                }
            }else {
                //不止还有一个
                if (arr.contains("&")){
                    arr.remove("&");
                    retlist.addAll(arr);
                    retlist.addAll(dealwithFollowNon(key,s.substring(non.length(),s.length())));
                }else{
                    retlist.addAll(arr);
                }
            }
        }
        return retlist;
    }
    private String Ishavenon(ArrayList<String> arr){
        for (String s : arr) {
            if (nonterminalArraylist.contains(s)){
                return s;
            }
        }
        return "false";
    }
    public ArrayList<String> Follow(String nonterminal){
        ArrayList<String> usednonlist = new ArrayList<>();
        usednonlist.add(nonterminal); // 用来循环替换非终结符号
        ArrayList<String> arr = pretentFollow(nonterminal);
        //一直替换，知道arr中没有非终结符号
        String non;
        while (!(non = Ishavenon(arr)).equals("false")){
            arr.addAll(pretentFollow(non));
            usednonlist.add(non);
            arr.removeAll(usednonlist);
        }
        filterepeat(arr);
        return arr;
    }

    private ArrayList<String> pretentFollow(String nonterminal) {
        ArrayList<String> retlist = new ArrayList<>();
        if (nonterminal.equals(startsymbol)){
            retlist.add("$");
        }
        // 先找到所有含有该nonterminal的产生式
        for (String s : nonterminalMap.keySet()) {
            // s是非终结符号的前键
            ArrayList<String> arrayList = nonterminalMap.get(s);
            for (String s1 : arrayList) {
                if (s1.contains(nonterminal)){
                    //分三种情况进行讨论
                    //1.在最末端
                    if (s1.endsWith(nonterminal)){
                        // 直接加入前键
                        retlist.add(s);
                    }else {
                        int index = s1.indexOf(nonterminal)+nonterminal.length();
                        s1 = s1.substring(index,s1.length());
                        retlist.addAll(dealwithFollowNon(s,s1));
                    }

                }
            }
        }
        filterepeat(retlist);
        return retlist;
    }

    private void filterepeat(ArrayList<String> retlist) {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(retlist);
        retlist.clear();
        retlist.addAll(hashSet);
    }

    public void generateSLR(){
        // 利用字符拼接功能，打印出所要求的表,要记录的是一个对象(state symbol state)
        // 首先从开始符号进行推到,构建最开始的项目集合
        // 这张表用来说明下一次推到用到的终结符和非终结符号
        // 应该用一个队列来进行拓扑管理,同时给每一个状态添加状态数字
        int state = 0;
        Queue<SLRItemSet> manageQueue = new ArrayDeque<>(); // 这个是操作队列
        ArrayList<SLRItemSet> storageQueue = new ArrayList<>(); // 这个是存储队列
        SLRItemSet slrItemSet = new SLRItemSet(this);
        SLRItem slrItem = new SLRItem(startsymbol,nonterminalMap.get(startsymbol).get(0));
        slrItemSet.add(slrItem);
        slrItemSet.getclosure();
        slrItemSet.setStatenum(getstate());
        storageQueue.add(slrItemSet);
        manageQueue.add(slrItemSet);
        //队列为空的时候，这时候才结束
        while (!manageQueue.isEmpty()){
            SLRItemSet slrItemSet1 = manageQueue.poll();
            System.out.println(slrItemSet1.getStatenum());
            HashSet<String> hashSet = slrItemSet1.getNextstringset();
            for (String s : hashSet) {
                if (s ==null) continue;
                SLRItemSet tmp = new SLRItemSet(slrItemSet1,s);
                System.out.print("加入"+s+"之后");
                tmp.getclosure();
                int mstate;
                if ((mstate = getstateFrompre(storageQueue,tmp))==-1){
                    tmp.setStatenum(getstate());
                    getstateFrompre(storageQueue,tmp);
                    System.out.println("到达的状态是"+tmp.getStatenum());
                    manageQueue.add(tmp);
                    storageQueue.add(tmp);
                }else {
                    System.out.println("到达的状态是"+mstate);
                }
            }
        }
        System.out.println("总共的状态数量是"+storageQueue.size());
    }

    private int getstateFrompre(ArrayList<SLRItemSet> storageQueue, SLRItemSet tmp) {
        for (SLRItemSet slrItemSet : storageQueue) {
            if (slrItemSet.equals(tmp)){
                return slrItemSet.getStatenum();
            }
        }
        return -1;
    }

    public static int state = 0;
    private int getstate() {
        int ret = state;
        state++;
        return ret;
    }

    private void printItemSet(SLRItemSet slrItemSet1) {
        System.out.println(slrItemSet1.toString());
        HashSet<SLRItem> hashSet = slrItemSet1.getclosure();
        for (SLRItem slrItem : hashSet) {
            System.out.println(slrItem);
        }
        System.out.println("------------------------------");
    }
}
