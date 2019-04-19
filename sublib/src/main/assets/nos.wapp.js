/**
 *
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var $F = {};
$F.CHECK_STATUS_TIMEOUT = 3000;
$F.HTTP_TIMEOUT = 35000;
$F.queryParams = {
    'TRID': 'trid',
    'API_KEY': 'apikey',
    'OPERATOR': 'operator',
    'REFERENCE': 'reference',
    'MSISDN': 'msisdn',
    'PRICE': 'price',
    'USER_ID': 'userid',
    'RETURN_URL': 'returnurl',
    'SIGN': 'sign',
    'COUNTRY': 'country',
    'PIN': 'pin',
    'COUPONCODE': 'couponcode',
    'ORIGIN': 'origin',
    'PARENT_URL': 'parenturl',
    'LANGUAGE': 'language',
    'CURRENCY': 'currency',
    'REDIRECT_TIMEOUT': 'redirecttimeout',
    'IMAGE': 'image',
    'USERBACK': 'userback',
    'COMPACTVIEW': 'compactview',
    'MCCMNC': 'mccmnc',
    'COMMAND_NAME': 'commandname',
    'PACKAGE_PREVIEW': 'packagePreview',
    'SEP_SIGN': 'sepsign',
    'SHOW_UNSUBSCRIBE': 'show-unsubscribe',
    'CAPTURE_TIME': 'captureTime',
    'ENC_MSISDN': 'encmsisdn'
};

$F.checkStatusVar = null;

$F.captureStartTime = null;
$F.captureEndTime = null;
$F.captureTimeStamp = null;
$F.captureSign = null;
$F.external = null;

$F.byId = function byId(id) {
    if (id instanceof Element) {
        return id;
    }
    return document.getElementById(id);
};

$F.byClass = function byClass(className) {
    if (className instanceof Element) {
        return className;
    }
    return document.getElementsByClassName(className);
};

$F.hasClass = function (id, className) {
    if ($F.byId(id) === undefined || $F.byId(id) === null) {
        return true;
    }
    return $F.byId(id).className.match(new RegExp('(\\s|^)' + className + '(\\s|$)')) !== null;
};
$F.isHidden = function (id) {
    return $F.hasClass(id, 'hidden');
};
$F.addClass = function addClass(id, className) {
    if ($F.byId(id) !== undefined && $F.hasClass(id, className) === false) {
        $F.byId(id).className = $F.byId(id).className + ' ' + className;
    }
};
$F.removeClass = function removeClass(id, className) {
    if ($F.hasClass(id, className)) {
        $F.byId(id).className = $F.byId(id).className.replace(new RegExp('(\\s|^)' + className + '(\\s|$)'), ' ');
    }
};
$F.toggleClass = function toggleClass(id, className) {
    if ($F.hasClass(id, className)) {
        $F.removeClass(id, className);
    } else {
        $F.addClass(id, className);
    }
};
$F.toggleAttribute = function toggleAttribute(id, attributeName, attributeValue) {
    var el = $F.byId(id);
    if (el.hasAttribute(attributeName)) {
        el.removeAttribute(attributeName);
    } else {
        el.setAttribute(attributeName, attributeValue);
    }
};
$F.disabled = function enabled(id, value) {
    var el = $F.byId(id);
    if (el !== null && el !== undefined) {
        el.disabled = value;
    }
};
$F.enterPress = function (event, func) {
    if (event.keyCode == 13) {
        if (func !== null && func != undefined)
            func();
    }
};
$F.redirect = function (url, timeout, statusElement) {
    timeout = timeout || 0;
    setTimeout(function () {
        //OLD ONE redirection
        //window.top.location.href =url;
        self.location.href = url;
    }, timeout);
};
$F.redirectInParent = function (url, timeout, statusElement) {
    timeout = timeout || 0;
    setTimeout(function () {
        window.top.location.href = url;
    }, timeout);
};
$F.redirectInNewWindow = function (url, timeout, statusElement) {
    timeout = timeout || 0;
    setTimeout(function () {
        window.open(url, '_blank');
    }, timeout);
};
$F.instantRedirect = function (url) {
    self.location.href = url;
    return;
};
$F.closeByTimeout = function (redirectUrl, timeout, trid, status) {
    $F.set('status', status);
    if (timeout === 0) {
        if ($F.byId('holder-main-page') !== null) {
            $F.addClass('holder-main-page', 'hidden');
        }
        $F.instantRedirect(redirectUrl);
        return;
    }
    setTimeout(function () {
        $F.closeWidget(redirectUrl);
    }, timeout * 1000);
};
$F.closeWidget = function closeWidget(redirectUrl) {
    $F.redirect(redirectUrl);
};
$F.isIE = function () {
    var myNav = navigator.userAgent.toLowerCase();
    return (myNav.indexOf('msie') !== -1) ? parseInt(myNav.split('msie')[1]) : false;
};
$F.createXhr = function (method) {
    var msie = $F.isIE();
    if (msie <= 8 && (!method.match(/^(get|post|head|put|delete|options)$/i) ||
        !window.XMLHttpRequest)) {
        return new window.ActiveXObject("Microsoft.XMLHTTP");
    } else if (window.XMLHttpRequest) {
        return new window.XMLHttpRequest();
    }

    throw minErr('$httpBackend')('noxhr', "This browser does not support XMLHttpRequest.");
};

$F.set = function (key, value) {
    $F.values = $F.values || {};
    $F.values[key] = value;
};

$F.get = function (key) {
    return $F.values[key];
};

$F.command = function (command, cb, timeoutcb) {
    if (self !== top) {
        command.parent = document.referrer;
    }

    command.w = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
    command.h = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
    command.external = $F.external;
    command.request_id = $F.requestId;
    command.xctkn = $F.xctkn;
    command.xctkns = $F.xctkns;

    var options = {url: '/payment/widget/command?' + $F.objToQueryStr(command), method: 'GET'};
    $F.http(options, function (err, data) {
        if (cb) {
            cb(err, data);
        }
        if (err) {
            console.log(err);
        } else {
            if (data.body !== "" && data.status === 200) {
                if (!$F.isHtmlPage(data.body)) {
                    $F.executeCommandCb(JSON.parse(data.body));
                }
            }
        }
    }, timeoutcb);
};

$F.isHtmlPage = function (value) {
    return value.indexOf('<html') != -1;
};

$F.executeCommandCb = function (commandCbData) {
    if ('change-main' === commandCbData.name) {
        if (window.history && window.history.pushState) {
            var historyObj = {};
            historyObj.command = commandCbData;
            historyObj.values = $F.values;
            history.pushState(historyObj, "");
        }
        var el = $F.byId('holder-main-page');
        el.innerHTML = commandCbData.content;
        var scriptElements = el.getElementsByTagName('script');
        for (var i = 0; i < scriptElements.length; i++) {
            eval(scriptElements[i].innerHTML);
        }
    } else if ('update-data' === commandCbData.name) {
        $F.set('params', $F.util.urlToObj(commandCbData.data));
    } else if ('array' === commandCbData.name) {
        for (var c in commandCbData.commands) {
            $F.executeCommandCb(commandCbData.commands[c]);
        }
    } else if ('redirect' === commandCbData.name) {
        $F.redirect(commandCbData.url, commandCbData.timeout);
    }

};
/*$F.stopCheckingStatus = function(){
 clearTimeout($F.checkingStatusInteval);
 };*/

$F.checkStatus = function (timeout) {
    $F.checkStatusVar = setTimeout(function () {
        $F.checkStatusCommand(timeout);
    }, timeout);
};
$F.checkForAuthorization = function (timeout) {
    $F.checkStatusVar = setTimeout(function () {
        $F.checkForAuthorizationCommand(timeout);
    }, timeout);
};
$F.checkStatusFromRedirect = function (timeout) {
    $F.checkStatusVar = setTimeout(function () {
        $F.checkStatusFromRedirectCommand(timeout);
    }, timeout);
};
/*$F.stopCheckingStatus = function(){ $F.stopCheckingFlag = true; };*/

$F.checkStatusCommand = function (timeout) {
    var command = $F.clone($F.values['params']);
    command.commandname = 'check-status';
    $F.command(command, function (err, data) {
        if (data.status === 302 && data.location !== undefined) {
            self.location.href = data.location;
            return;
        }
        if (data.status === 403) {
            self.location.href = "/payment/static/error/403.html";
        }
        if (data.status !== 200 || data.body === null || data.body === '') {
            $F.checkStatus(timeout);
        }
    }, function (e) {
    });
};

$F.checkForAuthorizationCommand = function (timeout) {
    var command = $F.clone($F.values['params']);
    command.commandname = 'check-for-authorization';
    $F.command(command, function (err, data) {
        if (data.status === 302 && data.location !== undefined) {
            self.location.href = data.location;
            return;
        }
        if (data.status !== 200 || data.body === null || data.body === '') {
            $F.checkForAuthorization(timeout);
        }
    }, function (e) {
    });
};

$F.checkStatusFromRedirectCommand = function (timeout) {
    var command = $F.clone($F.values['params']);
    command.commandname = 'check-status-from-redirect';
    $F.command(command, function (err, data) {
        if (data.status === 302 && data.location !== undefined) {
            self.location.href = data.location;
            return;
        }
        if (data.status !== 200 || data.body === null || data.body === '') {
            $F.checkStatusFromRedirect(timeout);
        }
    }, function (e) {
    });
};

$F.startCheckingStatus = function (timeout) {
    $F.startCheckingStatusCommand(timeout);
};

$F.startCheckingStatusCommand = function (trid, timeout) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'start-checking-status';
    command.trid = trid;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.checkStatusUntilAuthorized = function (timeout) {
    setTimeout(function () {
        $F.checkStatusUntilAuthorizedCommand(timeout);
    }, timeout);
};

$F.checkStatusUntilAuthorizedCommand = function (timeout) {
    var command = $F.clone($F.values['params']);
    command.commandname = 'check-status-until-authorized';
    $F.command(command, function (err, data) {
        if (data.status !== 200 || data.body === null || data.body === '') {
            $F.checkStatusUntilAuthorized(timeout);
        }
    }, function (e) {
    });
};
$F.unsubscribe = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;

    var command = $F.clone($F.values['params']);
    command.commandname = 'unsubscribe';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.confirmUnsubscribe = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;

    var command = $F.clone($F.values['params']);
    command.commandname = 'confirm-unsubscribe';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.showUnsubscribe = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;

    var command = $F.clone($F.values['params']);
    command.commandname = 'unsubscribe';
    command['show-unsubscribe'] = true;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.showUnsubscribe();
    });
};
$F.submitPin = function () {
    var pin = $F.byId('pinInput').value;
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-pin';
    command.pin = pin;
    command.captureTime = $F.captureEndTime;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.submitSmsWithPin = function (transactionId, sms) {
    $F.nextActionInitiated = true;
    clearTimeout($F.checkStatusVar);
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-sms-with-pin';
    command.transactionId = transactionId;
    command.sms = sms;
    delete command.pin;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.submitHandshakeSms = function (transactionId, sms, smsSender) {
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'send-handshake-confirm-sms';
    if (smsSender !== undefined) {
        command.smssender = smsSender;
    }
    command.transactionId = transactionId;
    command.sms = sms;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.submitPinRetrieverSms = function (transactionId, sms, retrieverStatus) {
    $F.nextActionInitiated = true;
    clearTimeout($F.checkStatusVar);
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-pin-retriever-sms';
    command.transactionId = transactionId;
    command.sms = sms;
    command.retrieverStatus = retrieverStatus;
    delete command.pin;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};

$F.submitHandshakeRetrieverSms = function (transactionId, sms, retrieverStatus) {
    $F.nextActionInitiated = true;
    clearTimeout($F.checkStatusVar);
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-handshake-retriever-sms';
    command.transactionId = transactionId;
    command.sms = sms;
    command.retrieverStatus = retrieverStatus;
    delete command.pin;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};

$F.onSmsSent = function (transactionId, isSent) {
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'on-sms-sent';
    command.transactionId = transactionId;
    command.isSmsSent = isSent;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
}
$F.submitOperator = function (operator) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-operator';
    command.operator = operator;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.submitOperator();
    });
};


$F.submitMsisdnValue = function (msisdn) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-msisdn';
    command.msisdn = msisdn;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.submitMsisdn();
    });

}

$F.submitMsisdn = function () {
    var msisdn = $F.byId('msisdnInput').value;
    $F.submitMsisdnValue(msisdn);
};

$F.submitMsisdnAndEmail = function () {
    var msisdnInput = $F.byId('msisdnInput');
    var msisdn = (msisdnInput !== null && msisdnInput !== undefined) ? msisdnInput.value : "";
    var email = $F.byId('emailInput').value;
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-msisdn-and-email';
    command.msisdn = msisdn;
    command.email = email;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.submitMsisdnAndEmail();
    });
};

$F.submitExternalMsisdnValue = function (msisdn) {

    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-external-msisdn';
    command.msisdn = msisdn;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.submitExternalMsisdn();
    });

}

$F.submitExternalMsisdn = function () {
    var msisdn = $F.byId('msisdnInput').value;
    $F.submitExternalMsisdnValue(msisdn);
};


$F.submitScratchcard = function (cardcode, cardid) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-scratchcard';
    command.cardcode = cardcode;
    command.cardid = cardid;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.submitCoupon = function () {
    var coupon = $F.byId('couponInput').value;
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'submit-coupon';
    command.coupon = coupon;
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.submitCoupon();
    });
};
$F.confirm = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'confirm';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.androidConfirm = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'android-confirm';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};
$F.getRedirectUrl = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'redirect';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};
$F.decline = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = "decline";
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};
$F.cancelCommand = function () {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = "cancel-transaction";
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.moConfirm = function () {
    if ($F.moConfirmPress === true) {
        return;
    }
    $F.moConfirmPress = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'select-package';
    command.moConfirm = 'true';
    $F.command(command, function (err, data) {
        delete $F.moConfirmPress;
    }, function (e) {
        $F.checkStatus($F.CHECK_STATUS_TIMEOUT);
    });
};
$F.selectPackage = function (price, packagePreview) {
    packagePreview = packagePreview || false;
    if ($F.selectPackagePress === true) {
        return;
    }
    $F.selectPackagePress = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'select-package';
    command.packagePreview = packagePreview;
    command.price = price;
    $F.command(command, function (err, data) {
        delete $F.selectPackagePress;
    }, function (e) {
        $F.selectPackage(price, packagePreview);
    });
};

$F.clone = function (obj) {
    var copy;
    if (null == obj || "object" != typeof obj) return obj;
    if (obj instanceof Date) {
        copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }
    if (obj instanceof Array) {
        copy = [];
        for (var i = 0, len = obj.length; i < len; i++) {
            copy[i] = $F.clone(obj[i]);
        }
        return copy;
    }
    if (obj instanceof Object) {
        copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = $F.clone(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported. Its: " + (typeof obj));
};

$F.http = function (options, cb, timeoutcb) {
    var msie = $F.isIE();
    var xmlhttp = $F.createXhr(options.method);

    xmlhttp.onreadystatechange = function () {
        if (xmlhttp.readyState === 4) {
            if (xmlhttp.status === 200) {
                cb(null, {
                    status: xmlhttp.status,
                    statusText: (msie < 10 ? "" : xmlhttp.statusText),
                    body: (('response' in xmlhttp) ? xmlhttp.response : xmlhttp.responseText)
                });
            } else {
                cb(null, {
                    status: xmlhttp.status,
                    statusText: (msie < 10 ? "" : xmlhttp.statusText),
                    body: (('response' in xmlhttp) ? xmlhttp.response : xmlhttp.responseText),
                    location: xmlhttp.getResponseHeader('Location')
                });
            }
        }
    };

    xmlhttp.ontimeout = function (e) {
        timeoutcb ? timeoutcb(e) : "";
    }

    xmlhttp.open(options.method, options.url);
    xmlhttp.timeout = $F.HTTP_TIMEOUT;

    if (options.data !== undefined) {
        xmlhttp.send(JSON.stringify(options.data));
    } else {
        xmlhttp.send();
    }

    return xmlhttp;
};
$F.util = {
    urlToObj: function (url) {
        var obj = {};
        url.replace(
            new RegExp("([^?=&]+)(=([^&]*))?", "g"),
            function ($0, $1, $2, $3) {
                obj[$1] = $3;
            }
        );
        for (var prop in obj) {
            if (obj.hasOwnProperty(prop)) {
                if (obj[prop] === '') {
                    delete obj[prop];
                } else {
                    obj[prop] = obj[prop].replace(/\+/g, "%20")
                    obj[prop] = decodeURIComponent(obj[prop]);
                }
            }
        }
        return obj;
    }
};
$F.findVisiblePage = function () {
    if ($F.isHidden('holder-main-page') === false) {
        return 'holder-main-page';
    }
    if ($F.isHidden('holder-subpage-help') === false) {
        return 'holder-subpage-help';
    }
    if ($F.isHidden('holder-subpage-languages') === false) {
        return 'holder-subpage-languages';
    }
    return undefined;
};

$F.showHidePage = function (id) {
    if ($F.hasClass(id, 'hidden')) {
        var visibleEl = $F.findVisiblePage();
        if (visibleEl !== undefined) {
            $F.addClass(visibleEl, 'hidden');
            var element = $F.byId(id);
            $F.prevVisible = visibleEl;
        }
        $F.removeClass(id, 'hidden');
    } else {
        $F.addClass(id, 'hidden');
        if ($F.prevVisible !== undefined) {
            $F.removeClass($F.prevVisible, 'hidden');
            delete $F.prevVisible;
        }
    }
};
$F.query = function (selector) {
    return document.getElementsByClassName(selector);
};
$F.selectValue = function (id) {
    var e;
    if (id instanceof Element)
        e = id;
    else
        e = document.getElementById(id);
    if (e === null)
        return null;
    return e.options[e.selectedIndex].value;
};
$F.selectValueByClass = function (selector) {
    var e = document.getElementsByClassName(selector)[0];
    if (e === null)
        return null;
    return e.value;
};
$F.objToQueryStr = function (obj) {
    var str = [];
    for (var p in obj) {
        if (obj.hasOwnProperty(p)) {
            str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
        }
    }
    return str.join("&");
};
$F.cancel = function (redirectUrl) {
    if ($F.values.params.trid !== undefined) {
        $F.cancelCommand();
    } else {
        $F.closeWidget(redirectUrl);
    }
};
$F.transactionStatus = function (trid, status, itemAmount, itemName, currency, clientId, price, apiKey, errorMessage) {
    $F.values = $F.values || {};
    $F.values.transaction = {
        trid: trid,
        status: status,
        itemAmount: itemAmount,
        itemName: itemName,
        currency: currency,
        clientId: clientId,
        price: price,
        apiKey: apiKey,
        errorMessage: errorMessage
    };
};
$F.isInIFrame = function () {
    return window.location !== window.parent.location;
};

$F.openSmsEditorAndr = function (shortCode, keyword) {
    window.location.href = "sms://" + shortCode + "?body=" + keyword;
};

$F.openSmsEditorIOS = function (shortCode, keyword) {
    window.location.href = "sms://" + shortCode + "&body=" + keyword;
};

$F.submitExtractedWord = function (smsSender, confirmationKeyword) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    if (smsSender !== undefined) {
        command.smssender = smsSender;
    }
    command.confirmationkeyword = confirmationKeyword;
    command.commandname = 'extracted-word-confirm';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.bridgeRedirect = function () {
    var command = $F.clone($F.values['params']);
    command.commandname = 'bridge-redirect';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.submitCaptcha = function (captcha) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    if (captcha !== undefined) {
        command.captcha = captcha;
    }
    command.commandname = 'captcha-confirm';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};


$F.submitValidateSimtcha = function (answer, simtchaReference) {
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    if (answer !== undefined) {
        command.simtchaanswer = answer;
    }

    if (simtchaReference !== undefined) {
        command.simtchareference = simtchaReference;
    }
    command.commandname = 'simtcha-validate';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.veriprobeCheck = function () {
    console.log("testets");
    if ($F.nextActionInitiated === true) {
        return;
    }
    $F.nextActionInitiated = true;
    var command = $F.clone($F.values['params']);
    command.commandname = 'veriprobe-check';
    $F.command(command, function (err, data) {
        delete $F.nextActionInitiated;
    }, function (e) {
    });
};

$F.watchForHover = function (container) {
    var hasHoverClass = false;
    var lastTouchTime = 0;

    function enableHover() {
        // filter emulated events coming from touch events
        if (new Date() - lastTouchTime < 500) return;
        if (hasHoverClass) return;

        container.className += ' hasHover';
        hasHoverClass = true;
    }

    function disableHover() {
        if (!hasHoverClass) return;

        container.className = container.className.replace(' hasHover', '');
        hasHoverClass = false;
    }

    function updateLastTouchTime() {
        lastTouchTime = new Date();
    }

    document.addEventListener('touchstart', updateLastTouchTime, true);
    document.addEventListener('touchstart', disableHover, true);
    document.addEventListener('mousemove', enableHover, true);

    enableHover();
}

$F.jsonData = {};

$F.paymentCapture = function (transactionID, operator, apiKey, timestamp, sign, startTime, event, resize, sendRequest, callback) {
    var endTime = new Date().getTime();
    $F.captureStartTime = new Date().getTime();
    $F.jsonData["metadata"] = {};
    $F.jsonData["security"] = {"timestamp": timestamp, "signature": sign};
    $F.jsonData["transaction"] = transactionID;
    $F.jsonData["operator"] = operator;
    $F.jsonData["service"] = apiKey;
    html2canvas(document.body, {logging: false}).then(function (canvas) {
        try {
            var screenCaptureBase64 = canvas.toDataURL("image/png");
            if (resize) {
                screenCaptureBase64 = $F.resizeToDataURL(canvas, canvas.width * 0.5, canvas.height * 0.5);
            }
            $F.jsonData["imageB64"] = screenCaptureBase64.split(",")[1];
            if (sendRequest) {
                $F.recordMetricsWithSendRequest(event, startTime, endTime, callback);
            }
        } catch (e) {
            if (sendRequest) {
                $F.recordMetricsWithSendRequest(event, startTime, endTime, callback);
            }
        }
    }, function () {
        $F.jsonData["imageB64"] = "null";
        if (sendRequest) {
            $F.recordMetricsWithSendRequest(event, startTime, endTime, callback);
        }
    });
};

$F.generateUrl = function () {
    var host = window.location.host;
    if (host.indexOf("stage") === -1) {
        return "https://0kb6r9yuih.execute-api.eu-central-1.amazonaws.com/paymentcapture_api/paymentcapture";
    } else {
        return "https://z4omd1jcjh.execute-api.eu-west-1.amazonaws.com/test_stage/paymentcapture";
    }
}

$F.recordMetricsWithSendRequest = function (event, startTime, end, callback) {
    try {
        var endTime = end;
        var url = $F.generateUrl();
        if (endTime == null) {
            endTime = new Date().getTime();
        }
        var encoded = new TextEncoderLite('utf-8').encode(document.documentElement.outerHTML);
        $F.jsonData["domB64"] = base64js.fromByteArray(encoded);
        $F.jsonData["metadata"]["timeElapsed"] = endTime - startTime;
        $F.jsonData["metadata"]["cookies"] = encodeURIComponent(document.cookie);
        $F.jsonData["metadata"]["userAgent"] = navigator.userAgent;
        $F.jsonData["metadata"]["currentUrl"] = encodeURIComponent(window.location.href);
        $F.jsonData["metadata"]["referrer"] = encodeURIComponent(document.referrer);
        if (event.keyCode === 13) {
            $F.jsonData["metadata"]["buttonClickPosition"] = {"top": -1, "left": -1};
            $F.jsonData["metadata"]["pageClickPosition"] = {"top": -1, "left": -1};
        } else {
            var viewportHeight = $(window).height();
            var pageHeight = $(document).height();
            var clickLeft = event.clientX;
            var clickTop = event.clientY;
            var obj = document.getElementById("primaryButton");
            var objLeft = 0;
            var objTop = 0;
            var objWidth = obj.offsetWidth;
            var objHeight = obj.offsetHeight;
            if (obj.offsetParent) {
                do {
                    objLeft += obj.offsetLeft;
                    objTop += obj.offsetTop;
                } while (obj = obj.offsetParent);
                objTop = objTop - (pageHeight - viewportHeight);
            }
            var percentageButtonLeft = ((clickLeft - objLeft) / objWidth * 100).toFixed(2);
            percentageButtonLeft = percentageButtonLeft < 0 ? 0 : percentageButtonLeft;
            var percentageButtonTop = ((clickTop - objTop) / objHeight * 100).toFixed(2);
            percentageButtonTop = percentageButtonTop < 0 ? 0 : percentageButtonTop;
            var percentagePageLeft = (clickLeft / window.innerWidth * 100).toFixed(2);
            var percentagePageTop = (clickTop / window.innerHeight * 100).toFixed(2);
            $F.jsonData["metadata"]["buttonClickPosition"] = {"top": percentageButtonTop, "left": percentageButtonLeft};
            $F.jsonData["metadata"]["pageClickPosition"] = {"top": percentagePageTop, "left": percentagePageLeft};
        }
        $F.ajaxRequest(url, JSON.stringify($F.jsonData), "POST", callback);
    } catch (e) {
        $F.ajaxRequest(url, JSON.stringify($F.jsonData), "POST", callback);
    }
};

$F.ajaxRequest = function (url, data, method, callback) {
    $.ajax({
        url: url,
        contentType: "application/json",
        dataType: 'json',
        type: method,
        data: data,
        timeout: 2000,
        success: function () {
            $F.captureEndTime = new Date().getTime() - $F.captureStartTime;
            callback();
        },
        error: function () {
            $F.captureEndTime = new Date().getTime() - $F.captureStartTime;
            callback();
        }
    });
};

$F.paymentCaptureOnEnterPress = function (transactionID, operator, apiKey, timestamp, sign, startTime, event, resize, sendRequest, callback) {
    if (event.keyCode === 13) {
        $F.paymentCapture(transactionID, operator, apiKey, timestamp, sign, startTime, event, resize, sendRequest, callback);
    }
};

$F.resizeToDataURL = function (imgCanvas, width, height) {
    var canvas = document.createElement('canvas');
    var ctx = canvas.getContext('2d');
    canvas.width = width;
    canvas.height = height;
    ctx.drawImage(imgCanvas, 0, 0, width, height);
    return canvas.toDataURL("image/png");
};

$F.addPromiseForIE = function () {
    if (/MSIE \d|Trident.*rv:/.test(navigator.userAgent))
        document.write('<script src="/payment/js/promise.js""><\/script>');
};

$F.openNosWaitingPage = function(duration) {
    var modalWait = document.getElementById('loading-modal');
    modalWait.className += " modalDisplay";
    setTimeout($F.openNosConfirmModal, duration);
};

$F.openNosConfirmModal = function () {
    var modalWait = document.getElementById('loading-modal');
    modalWait.style.display = "none";
    modalWait = document.getElementById('confirm-modal');
    modalWait.className += " modalDisplay";

    var index = Math.floor(Math.random() * 4);
    var colors = ["#4bdbc5", "#4f60d2", "#eb84cd", "#6ea514"];
    try {
        $F.byId("modalPrimaryButton").style.backgroundColor = colors[index];
        $F.byClass("modalCancelButton")[0].style.borderColor = colors[index];
        $F.byClass("modalCancelButton")[0].style.color = colors[index];
    } catch (err) {
    }
}

$F.scrollWindow = function () {
    var h = 0.3 * document.body.scrollHeight;
    window.scrollTo(0, h);
}

window.onpopstate = function (event) {
    if (event.state !== undefined && event.state !== null && event.state.command !== undefined && event.state.values !== undefined) {
        $F.values = event.state.values;
        $F.executeCommandCb(event.state.command);
    }
};