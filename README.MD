# DSLModUpdater
## 1. 개요
`DSLModUpdater`는 `Java JDK 17`으로 만들어진 서버 모드 다운로드 매니저입니다.<br/>
DSL에서 공식적으로 모드서버를 연 서버의 경우 이 모드 메니저를 통해 편리하게 모드를 다운받으실 수 있으며, 이를 통하여 모드들을 수동적으로 찾는 수고를 덜 수 있습니다.

***이제 모드를 직접 다운로드 하는 시대는 끝났습니다. 신세대를 맞이합시다!***

## 2. 설치 및 사용
> [DSLModUpdater v1.1.0 다운로드 링크](https://drive.google.com/file/d/1eBToOC4Fyu7DPqkbBokAaH_r9r4FESJ5/view?usp=sharing)

### 패치노트 (v1.1.0)
Release Date : 2025.04.17.

#### 새로운 기능
- 이제부터 `.exe` `.bat`이 기본저긍로 탑재되어 배포됩니다.
- 대대적인 UI 업데이트
- 이제 모드 추가 신청을 하실 수 있습니다.
- 모드들은 이제부터 버전명으로 관리됩니다. (예) `2024FEB01` `2025JAN07` 등등

#### 버그 수정
- 윈도우에서 한글이 깨지던 오류를 수정했습니다.
- 자바 `UnsupportedClassVersionError`를 수정했습니다. `JAR`도 같이 배포되며, `Java`를 따로 설치할 필요가 없습니다.
- `temp` 폴더를 찾을 수 없다는 오류를 수정했습니다

### 2.1. 실행
설치 후 빈 폴더에 압축을 푸신 뒤, `DSLModUpdater.exe` 파일을 실행합니다. 만약 바이러스 검사로 실행이 되지 않는다면, `compile` 폴더의 `run.bat` 파일로 이를 대체할 수 있습니다.

![Readme_1](https://raw.githubusercontent.com/Dwk0910/DSLModUpdater/refs/heads/master/docs/1.png)<br/>
또는<br/>
![Readme_2](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/2.png?raw=true)<br/>
![Readme_3](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/3.png?raw=true)

---

![Readme_4](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/4.png?raw=true)<br/>

> `Enter`를 눌러 계속합니다.

---

### 2.2. `mods` 경로 넣기 (`mods`폴더의 `절대경로`를 알고, 이를 넣으실 수 있는 분은 2.3. 단계로 넘어가십시오)
![Readmd_5](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/5.png?raw=true)<br/>

> `Win + R`을 누릅니다<br/>

![Readme_6](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/6.png?raw=true)<br/>
![Readme_7](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/7.png?raw=true)<br/>
 
> `열기`란에 `%appdata%`를 넣은 뒤, `확인`버튼을 누릅니다.

![Readme_8](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/8.png?raw=true)<br/>

> 파일 탐색기가 열리면, `.minecraft` 폴더에 들어갑니다. 없는 경우 `Minecraft`를 먼저 설치하십시오.

![Readme_9](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/9.png?raw=true)<br/>

> `mods`폴더에 들어갑니다. 없을 경우 새로 만듭니다.

![Readme_10](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/10.png?raw=true)<br/>
![Readme_11](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/11.png?raw=true)<br/>

> 위의 주소창에서 `mods`를 우클릭 한 뒤, `주소 복사`를 클릭합니다.

![Readme_12](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/12.png?raw=true)<br/>

> 다시 `DSLModUpdater`로 돌아와, `Ctrl + V`로 붙여 넣습니다.

> `Enter`를 눌러 계속합니다.

---

### 2.3. 서버 모드 적용하기
![Readme_13](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/13.png?raw=true)<br/>
> `1`을 입력한 후, `Enter`를 누릅니다.

![Readme_14](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/14.png?raw=true)<br/>
> `y`또는 `yes`를 입력한 후, `Enter`를 누릅니다.

![Readme_15](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/15.png?raw=true)<br/>
서버 내 모든 모드들을 알아서 가져온 뒤 설치합니다.

![Readme_16](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/16.png?raw=true)<br/>
모드의 버전명이 일치한 것을 알 수 있습니다. (이 때, 입력한 `mods`폴더에 가보면 모드들이 설치되어 있는 것을 확인하실 수 있습니다.)

---

### 2.4. 업데이터 종료
![Readme_17](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/17.png?raw=true)<br/>
![Readme_18](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/18.png?raw=true)<br/>
![Readme_19](https://github.com/Dwk0910/DSLModUpdater/blob/master/docs/19.png?raw=true)<br/>

> `3(뒤로가기)` > `3(업데이터 종료)` > `y`를 눌러 업데이터를 종료합니다.

---

## 3. Credits
`@neatore`는 여러분들의 편하고 재밌는 서버생활을 위해 항상 코딩합니다.<br/>
버그제보는 `@neatore`또는 `@dongwan0910` DM으로 부탁드립니다.<br/>
**감사합니다.**

---

***Thanks for reading!***
<br/>
Copyright 2024-2025. DSL All rights reserved.
