package top.lrshuai.ai.common.consts;

/**
 * 通用常量类
 */
public interface Consts {
    /**
     * 成功标记
     */
    Integer SUCCESS_CODE = 200;
    String SUCCESS_MSG = "success";

    /**
     * 失败标记
     */
    Integer FAIL_CODE = 500;
    String FAIL_MSG = "fail";

    /**
     * 删除标记
     */
    interface DelFlag{
        int YES = 1;
        int NO = 0;
    }


}
