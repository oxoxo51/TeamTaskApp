function sendJson(id, dateStr){
    var jsondata = {
        'id': id,
        'dateStr' : dateStr
    };
    $.post("/ajax_task",
        jsondata,
        function(result){
            var res = "";
            var taskMap = new Array();
            var finishMap = new Array();
            var notyetMap = new Array();
            var otherMap = new Array();
            var taskTrn = result.taskTrn;
            var dateStr = result.dateStr;

            // flashメッセージのセット
            if (result.msg !== "") {
                if (result.msgStatus === "success") {
                    $('#flash_msg').html(
                        "<div class='col-xs-12 success alert alert-success alert-dismissable'>"
                            + "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>"
                            + "<strong>" + result.msg + "</strong>"
                        + "</div>"
                    )
                } else if (result.msgStatus === "error") {
                    $('#flash_msg').html(
                        "<div class='col-xs-12 success alert alert-danger alert-dismissable'>"
                            + "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>"
                            + "<strong>" + result.msg + "</strong>"
                        + "</div>"
                    )
                }
            }
            // タスクリスト作成
            for (var i in taskTrn) {
                var keyVal = "";
                if (taskTrn[i].operationUserName !== "") {
                    // 実施済：実施ユーザーあり
                    keyVal = "1," + taskTrn[i].operationUserName + "," + taskTrn[i].taskTrnId;
                } else if (("1" === taskTrn[i].operationFlg)
                    && (taskTrn[i].operationUserName === "")) {
                    // 未実施：実施対象ON、かつ、実施ユーザーなし
                    keyVal = "0," + taskTrn[i].mainUserName + "," + taskTrn[i].taskTrnId;
                } else {
                    // 実施対象外：実施対象OFF、かつ、実施ユーザーなし
                    keyVal = "-1," + taskTrn[i].taskTrnId;
                }
                taskMap[keyVal] = taskTrn[i];
            }
            // ソート
            taskMap = arraySortByKey(taskMap, 1);
            // 実施済、未実施、対象外それぞれString配列のMAPでHTMLを作成する
            for (var key in taskMap) {
                var keyArg = key.split(",");
                var taskReferURL = "../" + taskMap[key].taskName + "/refer";
                var htmlStr = "";
                if ("1" === keyArg[0]) {
                    // 実施済
                    if (!(keyArg[1] in finishMap)) {
                        // 実施者毎のヘッダ作成
                        htmlStr = "<tr class='warning'><th colspan='2'>実施者："
                                + taskMap[key].operationUserName
                                + "</th></tr>";
                        // 一度MAPに入れる
                        finishMap[keyArg[1]] = htmlStr;
                    } else {
                        htmlStr = finishMap[keyArg[1]];
                    }
                    htmlStr += getTaskHtmlLine(taskMap[key].taskName, keyArg[0],
                                                taskMap[key].taskTrnId, dateStr, taskReferURL);
                    finishMap[keyArg[1]] = htmlStr;
                } else if ("0" === keyArg[0]) {
                    // 未実施
                    if (!(keyArg[1] in notyetMap)) {
                        // 主担当者毎のヘッダ作成
                        htmlStr = "<tr class='warning'><th colspan='2'>主担当者："
                                + taskMap[key].mainUserName
                                + "</th></tr>";
                        // 一度MAPに入れる
                        notyetMap[keyArg[1]] = htmlStr;
                    } else {
                        htmlStr = notyetMap[keyArg[1]];
                    }
                    htmlStr += getTaskHtmlLine(taskMap[key].taskName, keyArg[0],
                                                taskMap[key].taskTrnId, dateStr, taskReferURL);
                    notyetMap[keyArg[1]] = htmlStr;
                } else {
                    // 対象外
                    htmlStr += getTaskHtmlLine(taskMap[key].taskName, keyArg[0],
                                                taskMap[key].taskTrnId, dateStr, taskReferURL);
                    otherMap[keyArg[1]] = htmlStr;
                }
            }
            // 未実施
            res = "<thead><tr class='danger'><th colspan='2'>未実施</th></tr></thead><tbody>";
            // ソート
            arraySortByKey(notyetMap, 1);
            for (key in notyetMap) {
                res += notyetMap[key];
            }
            res += "</tbody>";
            // 対象外
            res += "<thead><tr class='active'><th colspan='2'>実施不要</th></tr></thead></tbody>";
            for (key in otherMap) {
                res += otherMap[key];
            }
            res += "</tbody>";
            // 実施済
            res += "<thead><tr class='success'><th colspan='2'>実施済</th></tr></thead></tbody>";
            // ソート
            arraySortByKey(finishMap, 1);
            for (key in finishMap) {
                res += finishMap[key];
            }
/*
                res += ("<p>ID:" + taskTrn[i].taskTrnId + "</p>"
                + "<p>タスク名:" + taskTrn[i].taskName + "</p>"
                + "<p>担当者:" + taskTrn[i].mainUserName + "</p>"
                + "<p>実施者:" + taskTrn[i].operationUserName + "</p>"
                + "<p>実施対象:" + taskTrn[i].operationFlg + "</p>");
            }
*/
            $('#ajax_list').html(res);
        },
        "json"
    );
}
function sendJsonTop(id){
    var jsondata = {
        'id': id
    };
    $.post("/ajax_task_top",
        jsondata,
        function(result){
            var res = "";
            var taskMap = new Array();
            var taskTrn = result.taskTrn;
            var dateStr = result.dateStr;

            // flashメッセージのセット
            if (result.msg !== "") {
                if (result.msgStatus === "success") {
                    $('#flash_msg').html(
                        "<div class='col-xs-12 success alert alert-success alert-dismissable'>"
                            + "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>"
                            + "<strong>" + result.msg + "</strong>"
                        + "</div>"
                    )
                } else if (result.msgStatus === "error") {
                    $('#flash_msg').html(
                        "<div class='col-xs-12 success alert alert-danger alert-dismissable'>"
                            + "<button type='button' class='close' data-dismiss='alert' aria-hidden='true'>&times;</button>"
                            + "<strong>" + result.msg + "</strong>"
                        + "</div>"
                    )
                }
            }
            // ヘッダ：（ボタン）/日付/チーム名/タスク名/担当者
            res = "<tr class='warning'><th/><th>日付</th><th>チーム名</th><th>タスク名</th><th>主担当者</th></tr>";
            for (var i in taskTrn) {
                res += getTaskHtmlLineTop(
                    taskTrn[i].taskTrnId,
                    taskTrn[i].dateStr,
                    taskTrn[i].teamName,
                    taskTrn[i].taskName,
                    taskTrn[i].mainUserName
                    );
            }
            $('#ajax_list').html(res);
        },
        "json"
    );
}

function getTaskHtmlLine(taskName, status, taskTrnId, dateStr, taskReferURL) {
    var taskUpdStr = "";
    switch (status) {
        // 実施済
        case '1':
            taskUpdStr = "戻す";
            break;
        // 未実施
        case '0':
            taskUpdStr = "実施";
            break;
        // 対象外→実施はできる
        case '-1':
            taskUpdStr = "実施";
            break;
    }
    return "<tr><td>"
            + createJsonButton(taskTrnId, dateStr, taskUpdStr)
            + "</td><td>"
            + "<a href=" + taskReferURL + ">" + taskName + "</a>"
            + "</td></tr>";
}

function getTaskHtmlLineTop(taskTrnId, dateStr, teamName, taskName, mainUserName) {
    return "<tr><td>"
            + createJsonButtonTop(taskTrnId)
            + "</td><td>"
            + dateStr
            + "</td><td>"
            + teamName
            + "</td><td>"
            + "<a href='../../" + teamName + "/task/" + taskName + "/refer'>" + taskName + "</a>"
            + "</td><td>"
            + mainUserName
            + "</td></tr>";
}

function createJsonButton(taskTrnId, dateStr, taskUpdStr) {
    return "<button class='json_button' onclick='sendJson("
            + taskTrnId + "," + dateStr + ")'>" + taskUpdStr + "</button>";
}

function createJsonButtonTop(taskTrnId) {
    return "<button class='json_button' onclick='sendJsonTop("
            + taskTrnId + ")'>" + "実施" + "</button>";
}

/* 配列をキーでソートする. sortDef=1:昇順/-1:降順 */
function arraySortByKey(array, sortDef) {
    // ソートした後の配列
    var sorted = {};
    // キーを格納する配列
    var keyArr = [];
    // キーを配列に詰める
    for (key in array) {
        if (array.hasOwnProperty(key)) {
            keyArr.push(key);
        }
    }
    // キーのソート
    if (1 === sortDef) {
        keyArr.sort();
    } else {
        keyArr.reverse();
    }
    // ソートした後の配列作成
    for (var i = 0; i < keyArr.length; i++) {
        sorted[keyArr[i]] = array[keyArr[i]];
    }
    return sorted;
}
