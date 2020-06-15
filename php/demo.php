<?php

$apiKey = '';
//网关地址，请联系客服
$host = '';
$merchant_no = "";

echo "\n";
print_r("==================订单创建==================");
$array = array(
    'orderNo'=>'1582352920518',
    'merchantNo'=>$merchant_no,
    'amount'=>700,
    'payMode'=>'ebank',
    'ts'=>time(),
    'notifyUrl'=>'https://www.baidu.com/',
    'returnUrl'=>'');
print_r($array);
echo "\n";
$sign_reduce=generate_sign_reduce($array);
echo "sign_reduce:".$sign_reduce;
echo "\n";
$sign = md5($sign_reduce.'key='.$apiKey);
echo "sign:".$sign;
//向数组里添加sign
$array['sign']=$sign;
print_r($array);

$create_order_link = $host.'/pass-order/#/create?'.$sign_reduce.'sign='.$sign;
echo "创建订单链接------>".$create_order_link;


echo "\n\n";
print_r("==================订单查询==================");

$url = $host.'/open/order/query';
$array = array(
    'merchantNo'=>$merchant_no,
    'orderNo'=>'1582352920518',
    'ts'=>time()
    );
print_r($array);

$sign_reduce=generate_sign_reduce($array);
echo "sign_reduce:".$sign_reduce;
echo "\n";
$sign = md5($sign_reduce.'key='.$apiKey);
echo "sign:".$sign;

//向数组里添加sign
$array['sign']=$sign;
print_r($array);

$result = json_post($array,$url);
echo"订单查询结果:".$result;

function json_post($array,$url){
    //转json
    $params = json_encode($array);
    //使用CURL发起psot请求 
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_HTTPHEADER, array(
        'Content-Type: application/json',
        'Content-Length: ' . strlen($params)
    ));
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_BINARYTRANSFER, true);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "POST");
    curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
     
    $result = curl_exec($ch);
    return $result;
    curl_close($ch);
}

function generate_sign_reduce($array){
//按键顺序正序排序
ksort($array);
//拼接 
$original_str = '';
foreach ($array as $key=>$value) {
    if(!empty($value) && strcasecmp('sign',$key) != 0){
        $original_str.=$key.'='.urlencode($value).'&';
    }
}
    echo "\n";
    return $original_str;
    echo "original_str:".$original_str; 
}

echo "\n\n";
print_r("==================验证签名==================");
$json = '{"amount":700,"orderNo":"1582352920518","merchantNo":"20200206151935264177807110","ts":1582354455,"payNo":"20200222145449450132236190","payStatus":-10,"payMode":"ebank","orderStatus":-40,"payTime":null,"sign":"3eba4517fa0a3ea71b17ee5ab0fe9248","name":"大海","bankNo":"6226620412731135","bankName":"中国光大银行"}';
//将json串转化成数组
$verify_array=json_decode($json,true);
print_r($verify_array);
echo "\n";
//获取sign值
foreach($verify_array as $key=>$value){
    if($key=='sign'){
       $get_sign= $value;
       print_r($get_sign);
    }
}
echo "\n";
$sign_reduce=generate_sign_reduce($verify_array);
echo "sign_reduce:".$sign_reduce;
echo "\n";
$sign = md5($sign_reduce.'key='.$apiKey);
echo "sign:".$sign;

echo "\n";
if(strcasecmp($get_sign,$sign) == 0){
     echo "sign_verify success";
}else{
     echo "sign_verify fail";
};


?>
