<!---
    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.
-->

# org.apache.cordova.geolocation

This plugin provides information about the device's location, such as latitude and longitude. 位置情報の共通のソースはグローバル ポジショニング システム （GPS） と IP アドレス、RFID、WiFi および Bluetooth の MAC アドレス、および GSM/cdma 方式携帯 Id などのネットワーク信号から推定される場所にもあります。 API は、デバイスの実際の場所を返すことの保証はありません。

この API は[W3C 地理位置情報 API 仕様][1]に基づいており、既に実装を提供しないデバイス上のみで実行します。

 [1]: http://dev.w3.org/geo/api/spec-source.html

**警告**: 地理位置情報データの収集と利用を重要なプライバシーの問題を発生させます。 アプリのプライバシー ポリシーは他の当事者とデータ (たとえば、粗い、罰金、郵便番号レベル、等) の精度のレベルでは共有されているかどうか、アプリが地理位置情報データを使用する方法を議論すべきです。 地理位置情報データと一般に見なされる敏感なユーザーの居場所を開示することができますので、彼らの旅行の歴史保存されている場合。 したがって、アプリのプライバシー ポリシーに加えて、強くする必要があります (デバイス オペレーティング システムしない場合そう既に)、アプリケーションに地理位置情報データをアクセスする前に - 時間のお知らせを提供します。 その通知は、上記の (例えば、 **[ok]**を**おかげで**選択肢を提示する) によってユーザーのアクセス許可を取得するだけでなく、同じ情報を提供する必要があります。 詳細については、プライバシーに関するガイドを参照してください。

## インストール

    cordova plugin add org.apache.cordova.geolocation
    

### Firefox OS Quirks

Create **www/manifest.webapp** as described in [Manifest Docs][2]. Add permisions:

 [2]: https://developer.mozilla.org/en-US/Apps/Developing/Manifest

    "permissions": {
        "geolocation": { "description": "Used to position the map to your current position" }
    }
    

## サポートされているプラットフォーム

*   アマゾン火 OS
*   アンドロイド
*   ブラックベリー 10
*   Firefox OS
*   iOS
*   Tizen
*   Windows Phone 7 と 8
*   Windows 8

## メソッド

*   navigator.geolocation.getCurrentPosition
*   navigator.geolocation.watchPosition
*   navigator.geolocation.clearWatch

## オブジェクト (読み取り専用)

*   Position
*   PositionError
*   Coordinates

## navigator.geolocation.getCurrentPosition

Returns the device's current position to the `geolocationSuccess` callback with a `Position` object as the parameter. エラーがある場合、 `geolocationError` コールバックに渡される、 `PositionError` オブジェクト。

    navigator.geolocation.getCurrentPosition(geolocationSuccess,
                                             [geolocationError],
                                             [geolocationOptions]);
    

### パラメーター

*   **geolocationSuccess**: 現在の位置を渡されるコールバック。

*   **geolocationError**: *(省略可能)*エラーが発生した場合に実行されるコールバック。

*   **geolocationOptions**: *(オプション)*地理位置情報のオプションです。

### 例

    // onSuccess Callback
    // This method accepts a Position object, which contains the
    // current GPS coordinates
    //
    var onSuccess = function(position) {
        alert('Latitude: '          + position.coords.latitude          + '\n' +
              'Longitude: '         + position.coords.longitude         + '\n' +
              'Altitude: '          + position.coords.altitude          + '\n' +
              'Accuracy: '          + position.coords.accuracy          + '\n' +
              'Altitude Accuracy: ' + position.coords.altitudeAccuracy  + '\n' +
              'Heading: '           + position.coords.heading           + '\n' +
              'Speed: '             + position.coords.speed             + '\n' +
              'Timestamp: '         + position.timestamp                + '\n');
    };
    
    // onError Callback receives a PositionError object
    //
    function onError(error) {
        alert('code: '    + error.code    + '\n' +
              'message: ' + error.message + '\n');
    }
    
    navigator.geolocation.getCurrentPosition(onSuccess, onError);
    

## navigator.geolocation.watchPosition

Returns the device's current position when a change in position is detected. デバイスを新しい場所を取得するとき、 `geolocationSuccess` コールバックを実行すると、 `Position` オブジェクトをパラメーターとして。 エラーがある場合、 `geolocationError` コールバックを実行すると、 `PositionError` オブジェクトをパラメーターとして。

    var watchId = navigator.geolocation.watchPosition(geolocationSuccess,
                                                      [geolocationError],
                                                      [geolocationOptions]);
    

### パラメーター

*   **geolocationSuccess**: 現在の位置を渡されるコールバック。

*   **geolocationError**: (省略可能) エラーが発生した場合に実行されるコールバック。

*   **geolocationOptions**: (オプション) 地理位置情報のオプションです。

### 返します

*   **String**: returns a watch id that references the watch position interval. The watch id should be used with `navigator.geolocation.clearWatch` to stop watching for changes in position.

### 例

    // onSuccess Callback
    //   This method accepts a `Position` object, which contains
    //   the current GPS coordinates
    //
    function onSuccess(position) {
        var element = document.getElementById('geolocation');
        element.innerHTML = 'Latitude: '  + position.coords.latitude      + '<br />' +
                            'Longitude: ' + position.coords.longitude     + '<br />' +
                            '<hr />'      + element.innerHTML;
    }
    
    // onError Callback receives a PositionError object
    //
    function onError(error) {
        alert('code: '    + error.code    + '\n' +
              'message: ' + error.message + '\n');
    }
    
    // Options: throw an error if no update is received every 30 seconds.
    //
    var watchID = navigator.geolocation.watchPosition(onSuccess, onError, { timeout: 30000 });
    

## geolocationOptions

地理位置情報の検索をカスタマイズするための省略可能なパラメーター`Position`.

    {maximumAge: 3000、タイムアウト: 5000、enableHighAccuracy: true};
    

### オプション

*   **enableHighAccuracy**： 最高の結果が、アプリケーションに必要があることのヒントを示します。 既定では、デバイスの取得を試みます、 `Position` ネットワーク ベースのメソッドを使用します。 このプロパティを設定する `true` 衛星測位などのより正確な方法を使用するためにフレームワークに指示します。 *(ブール値)*

*   **timeout**: The maximum length of time (milliseconds) that is allowed to pass from the call to `navigator.geolocation.getCurrentPosition` or `geolocation.watchPosition` until the corresponding `geolocationSuccess` callback executes. 場合は、 `geolocationSuccess` この時間内に、コールバックは呼び出されません、 `geolocationError` コールバックに渡される、 `PositionError.TIMEOUT` のエラー コード。 (と組み合わせて使用するときに注意してください `geolocation.watchPosition` の `geolocationError` 間隔でコールバックを呼び出すことができますすべて `timeout` ミリ秒 ！)*(数)*

*   **maximumAge**： 年齢があるミリ秒単位で指定した時間よりも大きくないキャッシュされた位置を受け入れます。*(数)*

### Android の癖

限り android 2.x エミュレーター地理位置情報の結果を返さない、 `enableHighAccuracy` オプションを設定します。`true`.

## navigator.geolocation.clearWatch

によって参照される、デバイスの場所への変更を見て停止、 `watchID` パラメーター。

    navigator.geolocation.clearWatch(watchID);
    

### パラメーター

*   **watchID**: の id、 `watchPosition` をクリアする間隔。(文字列)

### 例

    //オプション: 位置の変化を監視し、頻繁に使用//正確な位置取得法利用可能。
    //
    var watchID = navigator.geolocation.watchPosition(onSuccess, onError, { enableHighAccuracy: true });
    
    // ...later on...
    
    navigator.geolocation.clearWatch(watchID);
    

## Position

含まれています `Position` 座標、地理位置情報 API で作成されたタイムスタンプ。

### プロパティ

*   **coords**: 地理的座標のセット。*（座標）*

*   **タイムスタンプ**: 作成のタイムスタンプを `coords` 。*（日）*

## Coordinates

A `Coordinates` object is attached to a `Position` object that is available to callback functions in requests for the current position. It contains a set of properties that describe the geographic coordinates of a position.

### プロパティ

*   **緯度**: 10 度緯度。*(数)*

*   **経度**: 10 進度の経度。*(数)*

*   **高度**: 楕円体上のメートルの位置の高さ。*(数)*

*   **精度**: メートルの緯度と経度座標の精度レベル。*(数)*

*   **altitudeAccuracy**： メートルの高度座標の精度レベル。*(数)*

*   **見出し**: 進行方向、カウント、真北から時計回りの角度で指定します。*(数)*

*   **速度**： 毎秒メートルで指定されたデバイスの現在の対地速度。*(数)*

### Amazon Fire OS Quirks

**altitudeAccuracy**： 返すの Android デバイスでサポートされていません`null`.

### Android の癖

**altitudeAccuracy**： 返すの Android デバイスでサポートされていません`null`.

## PositionError

The `PositionError` object is passed to the `geolocationError` callback function when an error occurs with navigator.geolocation.

### プロパティ

*   **コード**: 次のいずれかの定義済みのエラー コード。

*   **メッセージ**: 発生したエラーの詳細を説明するエラー メッセージ。

### 定数

*   `PositionError.PERMISSION_DENIED` 
    *   Returned when users do not allow the app to retrieve position information. This is dependent on the platform.
*   `PositionError.POSITION_UNAVAILABLE` 
    *   Returned when the device is unable to retrieve a position. In general, this means the device is not connected to a network or can't get a satellite fix.
*   `PositionError.TIMEOUT` 
    *   Returned when the device is unable to retrieve a position within the time specified by the `timeout` included in `geolocationOptions`. When used with `navigator.geolocation.watchPosition`, this error could be repeatedly passed to the `geolocationError` callback every `timeout` milliseconds.