# be-was-2024
코드스쿼드 백엔드 교육용 WAS 2024 개정판
--- 
## 피드백 사항
- DTO 관련
  - 요청을 처리할 때 받은 정보들을 어떻게 처리할까 고민했습니다
  - 다른 자료구조들도 고민해봤지만 요청을 파싱한 결과들의 타입이 달라 사용하기 어렵다는 문제가 있었습니다
    - 전부 String으로 받는 방법은 나중에 사용할 때 데이터를 한번 더 가공해야 하므로 비효율적이라 생각했습니다 
  - 또한 리스트 같은 자료구조는 인덱스로 접근하기에 직관적이지 않아서 적합하지 않다고 생각했습니다
  - Map 또한 String,String 으로 받으면 모든 키 값을 상수로 저장해놓는 것이 좋은데, 이것또한 번거롭고 사용할 때 String을 다시 가공해야하는 문제는 남아있었습니다
  - 그래서 요청을 파싱한 결과들만을 가지고 있는 객체가 필요하고 또 편할것 같아서 만들었습니다
  - 만들고보니 적합한 이름이 DTO였고... 그래서 그렇게 이름 붙였으나 지금은 수정 했습니다
- 객체의 역할과 책임 분리 관련
  - 객체를 하나의 역할만 하도록 최대한 분리했습니다
--- 

## 공부할 것
1. HTTP
    - HTTP를 너무 대충 알고있다
    - 헤더의 어떤 부분이 어떤 것을 의미하는지는 알고있지만 형식을 정확하게는 모른다
    - 명확하게 한번 정리할 필요가 있다
2. 테스트 코드 작성 방법
   - 좋은 테스트 코드란?
   - 구현 하는데만 정신이 팔려서 테스트 코드를 생각을 못하고 있다..
--- 

## 고민 사항
- 테스트 코드를 어떻게 작성해야하나??
  - 구현을 해놓고 테스트 코드를 작성하려니 머리가 하얘졌다
  - 어떤 테스트를 어디서부터 해야할지도 감이 안잡히는 상태
  - 우선 테스트 코드를 작성하기 위해 각 클래스들이 하는 역할들을 정리해보기로 했다

### 클래스 별 역할
#### Request 멤버들
- HttpRequest
  - http request를 파싱해서 넣어놓는 객체
  - 요청마다 다른 객체가 만들어진다
- RequestParser
  - 파싱만을 담당하므로 요청마다 새로운 객체를 만들 필요가 없어서 싱글턴으로 구현
  - http request를 파싱한 다음 HttpRequest객체를 만들어서 반환해준다
  - 요청을 읽는 것은 이 객체의 책임이 아님
- RequestReader
  - InputStream을 읽는것만 담당하므로 마찬가지로 싱글턴으로 구현
  - request를 읽고 Map으로 반환해줌
- RequestHandler
  - http request의 중계자 역할
  - 외부에서는 http요청을 처리하기 위해 이 객체의 getRequest() 메서드만을 호출한다 
  - 요청마다 InputStream을 받아서 다른 객체가 만들어진다
  - RequestReader에게 read를 요청하고 받은 결과를 RequestPaser에게 주면서 파싱을 요청하여 파싱된 결과를 받아 넘겨준다

#### Response 멤버들
- HttpResponse
  - 요청에 따른 http response 결과를 넣어놓는 객체
- ResponseMaker
  - 요청 파싱과 마찬가지로 response 결과를 만드는 일은 요청마다 다르지 않으므로 싱글턴으로 구현
  - 반환 코드와 request 파싱 결과를 받아서 HttpResponse객체를 만들어서 반환해준다
  - 실제 응답을 보내는 것은 이 객체의 책임이 아님
- ResponseHandler
  - http response의 중계자 역할
  - 외부에서는 응답을 보내기위해 이 객체의 doResponse() 만을 호출한다
  - 요청마다 응답이 다르므로 HttpRequest, OutputStream을 받아 응답마다 새로운 객체가 만들어진다
  - ResponseMaker에게 응답을 만들어달라고 요청하여 받은 결과를 OutputStream으로 내보낸다

#### webserver 멤버들
- Repeater
  - 요청, 응답의 중계자 역할
  - 요청이 들어오면 run() 메서드가 실행된다
  - RequestHandler에게 요청을 처리해달라고 요청하고 HttpRequest 객체를 받는다
  - 받은 HttpRequest객체를 ResponseHandler에게 넘겨주어 응답을 보내달라고 요청한다
  - 만약 post요청이 들어와 클라이언트의 요청을 처리해아하면 요청 url과 body를 PostProcessor에게 넘겨주어 처리해달라고 요청한다
- PostProcessor
  - post요청을 처리하는 역할
  - 요청 url과 body를 받아서 요청에 따라 처리해준다
--- 
## 테스트 코드 작성 전략
1. Repeater -> RequestHandler에 InputStream을 주고 request read, parsing 요청
2. 파싱 결과를 Repeater가 받음
   - Repeater는 클라이언트가 작업을 요청(Post 메서드) 했으면 PostProcessor에게 요청 url과 요청 body를 주고 작업 처리 요청
3. Repeater -> ResponseHandler에 파싱 결과와 OutputStream을 주고 응답 메시지 생성과 응답 전송 요청

- 클라이언트의 각각 요청마다 위의 흐름으로 실행되므로 Request, Response, PostProcessor, Repeater로 나누어 검증할 예정
