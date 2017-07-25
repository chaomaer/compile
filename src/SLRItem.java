public class SLRItem {
    public String sentence;
    public int postion;
    public String nextString;
    public String key;
    public String content;
    public boolean isfinish = false;// 这个变量用来标记这个项目是否结束
    // 构建一个SLR项目需要注意
    // 为了使问题简单，终结符和非终结符均采用一个字符
    public SLRItem(String sentence){
        this.sentence = sentence;
        int index = this.sentence.indexOf("->");
        key = this.sentence.substring(0,index);
        content = this.sentence.substring(index+2,this.sentence.length());
        postion = -1;
        nextString = content.substring(postion+1,postion+2);
    }
    public SLRItem(SLRItem slrItem){
        this.sentence = slrItem.sentence;
        this.key = slrItem.key;
        content = slrItem.content;
        postion = slrItem.postion+1;
        if (postion == content.length()-1){
            isfinish = true;
        }else {
            nextString = content.substring(postion+1,postion+2);
        }
    }
    public SLRItem(String key,String content){
        this.sentence = key+"->"+content;
        this.key = key;
        this.content = content;
        postion = -1;
        nextString = content.substring(postion+1,postion+2);
    }

    @Override
    public String toString() {
        return "SLRItem{" +
                "sentence=" + sentence +
                ", postion=" + postion +
                ", nextString=" + nextString +
                ", key=" + key +
                ", content=" + content +
                ", isfinish=" + isfinish +
                '}';
    }
    //判断两个项目是否相等
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SLRItem)) return false;
        SLRItem tmp = (SLRItem) obj;
        if (tmp.sentence.equals(sentence)&&tmp.postion==postion){
            return true;
        }
        return false;
    }
}
