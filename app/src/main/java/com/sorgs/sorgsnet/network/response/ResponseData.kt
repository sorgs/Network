package com.sorgs.sorgsnet.network.response


/**
 * description: 接口返回数据封装.
 * {code:0,data:"",msg:""}
 * code：接口返回的code 一定不能为空
 * data：接口返回具体的数据结果 可能为空
 * msg：message 可用来返回接口的说明 可能为空
 *
 * @author Sorgs.
 * Created date: 2019/7/30.
 */
data class ResponseData<T>(
        var code: Int,
        var data: T?,
        var msg: String?
)
