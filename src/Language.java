import java.util.*;

public class Language {
    public ArrayList<String> terminalArraylist = new ArrayList<>();
    public HashMap<String,ArrayList<String>>  nonterminalMap =  new LinkedHashMap<>();
    public ArrayList<String> nonterminalArraylist = new ArrayList<>();
    public String startsymbol;
    private ArrayList<String> arrayList = new ArrayList<>();//这张表专门用来画图
    private ArrayList<String> visitnon = new ArrayList<>(); //这张表用来防止first死锁

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
    private ArrayList<String> dealwithFirstNon(String s){
        ArrayList<String> arrayList = new ArrayList<>();
        // 为递归回来使用，首先检查是否是终结符，如果是:
        String ter = Isstartwithter(s);
        if (ter != null){
            arrayList.add(ter);
        }else {
            // 首先得到s开始得到非终结符
            String non = Isstartwithnon(s);
            assert non != null;
            if (visitnon.contains(non)) return arrayList;
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
                    arrayList.addAll(dealwithFirstNon(s.substring(non.length(),s.length())));
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
        visitnon.add(nonterminal);
        ArrayList<String> retlist = new ArrayList<>();
        ArrayList<String> temps = nonterminalMap.get(nonterminal);
        for (String temp : temps) {
            String ter;
            if (( ter = Isstartwithter(temp))!=null){
                retlist.add(ter);
            }else {
                retlist.addAll(dealwithFirstNon(temp));
            }
        }
        visitnon.remove(nonterminal);
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
                while (s1.contains(nonterminal)){
                    if (s1.indexOf(nonterminal)+nonterminal.length()==s1.length()){
                        // 说明是最后一个
                        retlist.add(nonterminal);
                        break;
                    }else {
                        int index = s1.indexOf(nonterminal)+nonterminal.length();
                        s1 = s1.substring(index,s1.length());
                        retlist.addAll(dealwithFollowNon(s,s1));
                    }
                }
                if (s1.contains(nonterminal)){
                    //分三种情况进行讨论
                    //1.在最末端
                    if (s1.endsWith(nonterminal)){
                        // 直接加入前键
                        retlist.add(s);
                    }
                    if (!s1.endsWith(nonterminal)||s1.indexOf(nonterminal)!=s1.lastIndexOf(nonterminal)){
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
        inittable();
        while (!manageQueue.isEmpty()){
            ArrayList<String> table = new ArrayList<>();
            SLRItemSet slrItemSet1 = manageQueue.poll();
            int bstate = slrItemSet1.getStatenum();
//            System.out.println(bstate);
//            printItemSet(slrItemSet1); //just for test
//            System.out.println(slrItemSet1.getStatenum());
            HashSet<String> hashSet = slrItemSet1.getNextstringset();

            for (String s : hashSet) {
//                System.out.println("加入"+s+"之后");
                boolean flag = false;
                if (s ==null) {
                    // 说明这个项目集合该规约了
                    // 遍历集合，找到这条项目,获取编号，获取follow集合
                    for (SLRItem item : slrItemSet1.items) {
                        if (item.nextString == null){
                            ArrayList<String> followset = Follow(item.key);
                            int id = getid(item);
                            for (String s1 : followset) {
                                table.add(s1+"r"+id);
                            }
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) continue;
                SLRItemSet tmp = new SLRItemSet(slrItemSet1,s);
                tmp.getclosure();
                int estate;
                if ((estate = getstateFrompre(storageQueue,tmp))==-1){
                    tmp.setStatenum(getstate());
                    getstateFrompre(storageQueue,tmp);
                    estate = tmp.getStatenum();
//                    printItemSet(tmp);
                    manageQueue.add(tmp);
                    storageQueue.add(tmp);
                }else {
//                    System.out.println("和"+estate+"重复");
                }
                table.add(s+"s"+estate);
            }
            drawline(bstate,table);
        }
        System.out.println();
    }

    private int getid(SLRItem item) {
        int id = 0;
        String content = item.content;
        String key = item.key;
        for (String s : nonterminalMap.keySet()) {
            if (key.equals(s)){
                for (String s1 : nonterminalMap.get(s)) {
                    if (s1.equals(content)){
                        return id;
                    }else {
                        id += 1;
                    }
                }
            }else {
                id += nonterminalMap.get(s).size();
            }
        }
        return id;
    }

    private void drawline(int bstate, ArrayList<String> table) {
        System.out.println();
        boolean flag;
        System.out.printf("%-10s",bstate);
        for (String s : arrayList) {
            StringBuffer sb = new StringBuffer();
            flag = false;
            for (String s1 : table) {
                if (s1.startsWith(s)){
                    String ss = s1.substring(1);
                    if (ss.equals("r0")) {
                        sb.append("acc");
                    }else if (nonterminalMap.keySet().contains(s)){
                        sb.append(ss.substring(1));
                    }else sb.append(ss);
                    flag = true;
                }
            }
            if (flag){
                System.out.printf("%-10s",sb.toString());
            }
            if (!flag){
                System.out.printf("%-10s","");
            }
        }
    }

    private void inittable() {
        for (String s : terminalArraylist) {
            if (!s.equals("&")){
                arrayList.add(s);
            }
        }
        arrayList.add("$");
        arrayList.addAll(nonterminalMap.keySet());
        System.out.printf("%-10s","state");
        for (String s : arrayList) {
            if (!s.equals("&"))
                System.out.printf("%-10s",s);
        }
    }

    private int getstateFrompre(ArrayList<SLRItemSet> storageQueue, SLRItemSet tmp) {

        for (SLRItemSet slrItemSet : storageQueue) {
            if (slrItemSet.equals(tmp)){
//                System.out.println("重复");
//                printItemSet(slrItemSet);
//                System.out.println("---------");
//                printItemSet(tmp);
//                System.out.println("重复");
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
//        System.out.println(slrItemSet1);
        HashSet<SLRItem> hashSet = slrItemSet1.getclosure();
        for (SLRItem slrItem : hashSet) {
            System.out.println(slrItem);
        }
    }
}
