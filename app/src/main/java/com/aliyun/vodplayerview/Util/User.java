package com.aliyun.vodplayerview.Util;

public class User {
    private long userId;
    private String userName;
    private String psw;
    private int privilege;
    private String lines;
    private String groups;
    private String points;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public String getLines() {
        return lines;
    }

    public void setLines(String lines) {
        this.lines = lines;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getPrivilegeString() {
        String privilegeString;
        switch (this.privilege) {
            case Constant.ADMIN:
                privilegeString = "管理员";
                break;
            case Constant.LINE:
                privilegeString = "线级";
                break;
            case Constant.GROUP:
                privilegeString = "组级";
                break;
            case Constant.POINT:
                privilegeString = "点级";
                break;
            case Constant.IDCHANGE:
                privilegeString = "点(可管理ID)";
                break;
            default:
                privilegeString = "未知";
                break;
        }
        return privilegeString;
    }

    @Override
    public String toString() {
        return "User{userId=" + this.userId + ", userName='" + this.userName + '\'' + ", psw='" + this.psw + '\'' + ", " +"privilege=" + this.privilege + ", lines=" + lines + ",groups=" + groups+ ", points=" + points+ '}';
    }

    public String getShowData(String oriStr){
        if (oriStr.length()<3){
            oriStr="ALL";
        }else {
            oriStr= oriStr.substring(1, oriStr.length() - 1);
        }
        return oriStr;
    }
    public String getUserMsgToast() {
        String UserMsg = "工号：" + userId + "\n用户名：" + userName + "\n权限：" + getPrivilegeString() + "\n可查看线：" + getShowData(lines)+  "\n可查看组：" + getShowData(groups) +  "\n可查看点：" + getShowData(points);
        return UserMsg;
    }
    public String getUserMsgProfile(){
        return "工号：" + userId+"    " + getPrivilegeString() + "权限\n" + getShowData(lines)+  "/" + getShowData(groups) +  "/" + getShowData(points);
    }
}
