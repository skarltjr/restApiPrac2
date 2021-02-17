# restApiPrac2
스프링부트 버전업에 따라 변경점 부분 비교용


- EventResource
- new Link -> Link.of
- Errors serializer에         jsonGenerator.writeFieldName("errors");가 필요해졌다
- Errors를 테스트할 때            .andExpect(jsonPath("content[0].code").exists()) -> content -> errors
