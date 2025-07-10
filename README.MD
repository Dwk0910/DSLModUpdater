# DSLModUpdater
## 1. 개요
`DSLModUpdater`는 `Java 17`으로 만들어진 서버 모드 다운로드 매니저입니다.<br/>
DSL에서 공식적으로 모드서버를 연 서버의 경우 이 모드 메니저를 통해 편리하게 모드를 다운받으실 수 있으며, 이를 통하여 모드들을 수동적으로 찾는 수고를 덜 수 있습니다.

***이제 모드를 직접 다운로드 하는 시대는 끝났습니다. 신세대를 맞이합시다!***

## 2. 설치 및 사용
> [DSLModUpdater v2.0.0 SETUP 다운로드 링크](https://drive.google.com/file/d/1pxKPVpNKsqTVS7umtuvOCRDK5T1RDah8/view?usp=sharing)

### 2.1. 설치
압축을 푼 뒤, `run.bat` 파일을 실행합니다.

![image_1](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/1.png)

흰색 바탕에 회색 글씨로 쓰여진 메뉴가 선택된 메뉴입니다.
메뉴를 선택한 뒤 ENTER키를 눌러 선택할 수 있습니다.

'다음'을 선택하고 ENTER를 눌러 설치를 계속합니다.

![image_2](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/2.png)

EULA 계약서입니다. 꼼꼼히 읽어보시고 '다음'을 선택, ENTER를 눌러 동의하실 수 있습니다.

![image_3](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/3.png)

`y`또는 `yes`를 입력하고 ENTER를 눌러 설치를 계속합니다.

![image_4](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/4.png)

설치가 가능한 Minecraft의 버전을 자동으로 인식하여 띄워줍니다.
설치를 원하는 버전의 번호를 입력한 뒤, ENTER를 눌러 계속합니다.

이후 설치 완료 후 ENTER를 눌러 셋업파일을 마칩니다.

### 2.2. 실행
당신의 `문서` 폴더로 들어간 뒤, `DSLModUpdater` 폴더를 찾아 들어갑니다.
폴더 내 `run.bat` 파일을 실행합니다.

![image_5](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/5.png)

ENTER를 눌러 계속합니다

![image_6](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/6.png)

업데이터가 기본으로 마인크래프트 경로를 찾습니다.
이 창이 뜨지 않는 경우, `%appdata%`내 `.minecraft\mods`가 없는 것입니다.<br/>
마인크래프트를 %appdata% 내 `.minecraft` 폴더에 설치하지 않았을 경우 정상 동작이 안될 것입니다.
이를 참고하여 설치해 주시기 바랍니다. (v2.1.0에서 개선 예정)

`y`또는 `yes`를 입력하고 ENTER를 눌러 계속합니다

![image_7](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/7.png)
위, 아래 방향키와 ENTER로 메뉴를 선택할 수 있습니다.

| 메뉴이름 | 기능 |
| ------- | ---- |
| ICT서버 설정 | 검사에 사용될 ICT서버를 등록하고 관리합니다. ICT서버에 대해서는 후술 예정. |
| 수동 모드 적용하기 | 자동 적용에 실패하였을 경우 이 메뉴를 통해 수동으로 마인크래프트에 모드를 적용할 수 있습니다.<br/>**이 때 마인크래프트가 꺼져 있어야 합니다!**
| 설정 | `업데이터 초기화` `mods폴더 위치 변경` `기본 ICT서버 비우기`의 기능이 있습니다 |
| 업데이터 종료 | 업데이터를 종료합니다. |

#### ICT서버 설정하기

**ICT서버란?**<br/>
ICT는 **I**ntegrity **C**heck **T**arget (무결성 검사 대상) 의 축약어로, 쉽게말하여 모드들을 모아놓은 서버를 지칭합니다.<br/>
사용자는 자신이 다운로드 받을 원하는 모드들을 모아둔 ICT서버의 URL을 입력하여 이곳에 등록하며,<br/>
그 뒤로 DSLModUpdater는 ICT서버로부터 모드들의 정보를 불러와 사용자의 컴퓨터에 설치합니다.<br/>

**ICT서버 설정하기**<br/>
`ICT서버 설정` 메뉴에 들어갑니다.

![image_8](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/8.png)

`add`를 입력하고, ENTER를 누릅니다.

![image_9](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/9.png)

ICT서버의 URL을 입력합니다. ICT서버 URL은 각 서버 설명을 참고하거나, 서버 관리자에게 문의하여 받을 수 있을 것입니다. 이 설명에서는 예시로 DCS ICT서버 URL인 `http://mods.dslofficial.kro.kr' 를 사용합니다.

![image_10](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/10.png)

`y`또는 `yes`를 입력하고, ENTER를 눌러 계속합니다.

![image_11](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/11.png)

최신 버전의 이름과 모드 개수가 일치하면 `y`또는 `yes`를 입력하고 ENTER를 눌러 계속합니다.

![image_12](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/12.png)

등록할 이름을 입력한 뒤 ENTER를 눌러 등록을 마칩니다.

#### 자동 검사 서버로 등록하기

**자동 검사란?**<br/>
DSLModUpdater v2.0.0 의 신기능으로, 당신이 마인크래프트를 실행하게 되면 DSLModUpdater가 자동으로 실행되어 당신의 mods폴더를 읽고 무결성검사를 한 뒤 만약 잘못된 모드가 있다면 자동으로 모드를 다운로드 및 적용시킨 다음 마인크래프트가 켜지도록 도와줍니다.<br/>
자동 검사를 하려면, ICT서버가 적어도 하나는 등록되어 있어야 하며, 매번 ICT서버를 선택하는 번거로움을 줄이려면 등록을 원하는 ICT 서버를 **`기본 ICT 서버`** 로 만들어야 합니다. 이 파트에서는 추가한 ICT서버를 자동검사의 대상으로 만드는 방법을 설명합니다.

![image_13](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/13.png)

등록을 원하는 서버의 번호를 입력한 뒤, ENTER를 눌러 설정 창으로 들어갑니다.

![image_14](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/14.png)

`기본 ICT 서버로 만들기`를 선택하고 ENTER를 눌러 해당 ICT 서버를 기본 ICT서버로 만듭니다.

![image_15](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/15.png)

`자동검사 기본서버`가 뜨면 성공적으로 기본 ICT서버로 등록된 것입니다.

이후 `뒤로가기` -> `exit` 입력 후 ENTER -> `업데이터 종료`
를 눌러 업데이터를 종료합니다.

### 2.3. 구동 모습
![image_16](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/16.png)

마인크래프트의 플레이 버튼을 누릅니다.

![image_17](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/17.png)

새로운 `cmd` 창이 뜨며, 자동검사 서버가 등록되어 있다면 위와 같이 `기본 ICT 서버를 찾았습니다` 라는 말이 나오며 아무것도 하지 않아도 자동으로 검사를 진행합니다.

![image_18](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/18.png)

만약 자동검사 서버가 없거나, 1초 이내에 ENTER를 눌렀을 경우 이렇게 뜨며, 검사를 원하는 ICT서버의 번호를 입력한 뒤 ENTER를 눌러 검사를 시작할 수 있습니다.

![image_19](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/19.png)

만약 검사 결과, 모드가 모드 올바르다면 위와 같은 메시지를 출력한 뒤 업데이터는 바로 종료됩니다.

그러나 모드가 올바르지 않다면 아래와 같이 자동으로 설치 및 적용한 뒤 종료됩니다.

![image_20](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/20.png)

## 3. Credits
`@neatore`는 여러분들의 편하고 재밌는 서버생활을 위해 항상 코딩합니다.<br/>
버그제보는 `@neatore`또는 `@dongwan0910` DM으로 부탁드립니다.<br/>
**감사합니다.**

---

***Thanks for reading!***
<br/>
Copyright 2024-2025. DSL All rights reserved.
