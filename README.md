# Login 기능 Refactoring & Deep dive

이 Repositroy의 목적은 GbcCollector의 로그인 기능을 Refactoring함과 동시에 로그인 기능에 관한 기술들을 Deep dive하기 위함이다. 

Web 기술에 집중을 하기 위해 DB는 메모리로 사용한다. (향후 DB관련 오류학습을 위해 변경 예정)

로그인 기술을 사용, 구현하되, 기술이 내부에서 어떻게 작동하는지를 알기 위해 발전 단계별로 package를 나눠 구현해 나간다. 

향후 참고용 template으로 활용하기 위해 설명은 최대한 자세히 한다.

처음 package는 내부에서 사용하는 logic을 위한 domain과 사용자에게 서비스를 제공할 때 필요한 web으로 나눈다.



### Day 1

---



<img src="https://user-images.githubusercontent.com/76586084/184356747-ef3e60df-f590-46de-a5c3-44f914d02e1b.png" alt="image" style="zoom: 80%;" />

package의 구성 

MVC pattern으로 구현하고 향후 확장성을 고려하여 Interface를 활용했다.


### IndexPage
<img src="https://user-images.githubusercontent.com/76586084/184359227-9cf1eb57-0467-4df8-86e7-074b3c26f626.png" alt="image" style="zoom: 23%;" />


### memberInsertPage
<img src="https://user-images.githubusercontent.com/76586084/184360117-c2b33092-5d49-4421-a360-d694f9cb9b39.png" alt="image" style="zoom: 23%;" />

로그인 학습에 필요한 Page를 우선 구현한다. 기존 미흡하게 구현되었던 Thymeleaf 기능을 조금 Refactoring 하였다.

##### 1.

![image](https://user-images.githubusercontent.com/76586084/184359262-701b5392-cb94-4b18-968e-0d3b6b0dd73b.png)

> root 주소에서 @GetMapping을 활용해 Index page를 server 딴에서 전송하여 Thymeleaf 기능을 지원하게 하였다.



##### 2.

![image](https://user-images.githubusercontent.com/76586084/184359288-adac345b-7356-472d-83b0-06c7fd06d33f.png)

> RESTFUL URI활용하고 th:field 기능을 활용하기 위해 Model 객체를 생성하고 넣어주었다.

# 

### Day 2

---

##### Post 객체 확인

![image](https://user-images.githubusercontent.com/76586084/184472917-f2aa1a50-0ff6-4ac8-86e8-7c9a945d3444.png)

> memberInsertForm 페이지가 잘 연결되있는지 확인하기 위해 ResponseBody와 Log기능을 활용해보았다. (잘 넘어옴)







### Day 3

---

검증 오류 저장소와 검증 로직을 개발해보았다.

![image](https://user-images.githubusercontent.com/76586084/184534610-80beb886-4e1f-43c4-8f61-c809052a7536.png)



이중 emailTypeCheck과 nicknameDuplicateCheck의 메서드를 간단히 설명하면,

1. emailTypeCheck의 경우

![image](https://user-images.githubusercontent.com/76586084/184534622-3ff02a4a-1495-42cc-9560-3a4e3dd741d4.png)

위와같이 정규표현식과 Pattern class를 활용하였다. Pattern.compile()로 주어진 정규표현식으로부터 패턴을 만들고 matcher()를 활용해 패턴이 틀릴경우 True를 반환하는 로직이다. 



2. nicknameDuplicateCheck의 경우

우선 MemoryMemberRepository에

![image](https://user-images.githubusercontent.com/76586084/184534761-06ab8339-a4ad-40ae-aba0-79cc8cf8af5d.png)

findByNickName을 만든 후 이것을 MemberServiceImpl에서 활용했다.

![image](https://user-images.githubusercontent.com/76586084/184534769-a5a15935-48a0-4f17-ace0-418ccc1aa07f.png)

위 로직의 경우 저장소에 닉네임이 존재하지 않으면 False를 반환하는 method이다.

이 로직의 경우 Test를 해보았다.

![image](https://user-images.githubusercontent.com/76586084/184534787-50fe9414-6e49-48b3-be70-b92a7686697d.png)

잘 작동한다.



또한, Filed error를 넘어 Global error를 관리하기 위해 password와 passwordConfirm이 다를 경우를 상정해 보고 Global error를 만들어 보았다.

![image](https://user-images.githubusercontent.com/76586084/184534915-9b7ca345-c445-4fcc-9f3e-656832bb5ddf.png)



만들어진 프로젝트로 실행을 해보았다.

```java
2022-08-14 20:02:07.301  INFO 8296 --- [nio-8080-exec-1] c.g.g.l.w.v.ValidationMemberController   : errors = {privacyCheck=개인정보처리방침 동의는 필수입니다., password=비밀번호 입력은 필수입니다., termsCheck=이용약관 동의는 필수입니다., passwordConfirm=비밀번호 확인 입력은 필수입니다., nickname=중복된 닉네임 입니다., userEmail=이메일 형식에 맞춰 입력해주세요. ex) abcdefg@Naver.com, Name=이름 입력은 필수입니다.}
```

Log가 의도한대로 들어오는것을 확인할 수 있었다.



이제 화면에 errors들을 표시해 보았다.

##### Field-error

```html
<div style="margin-bottom:9px; margin-top:16px;"
							 th:classappend="${errors?.containsKey('name')} ? 'field-error' : _"
							 class="signup-name">이름</div>
                                 
<div class="field-error" th:if="${errors?.containsKey('name')}" th:text="${errors['name']}">
							이름
						</div>
```

##### Global-error

```html
<div th:if="${errors?.containsKey('globalError')}">
							<p class = "field-error" th:text="${errors['globalError']}">전체 오류 메시지</p>
						</div>
```



위를 적용한 화면은 아래와 같다.



![image](https://user-images.githubusercontent.com/76586084/184538152-58ed97c0-c7b8-4cb2-98c8-54c6b6ac267a.png)



























