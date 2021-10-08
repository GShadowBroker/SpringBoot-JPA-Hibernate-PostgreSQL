package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class DemoApplicationTests {

    private final Calculator calculator = new Calculator();

    @Test
    void itShouldAddNumbers() {
        // given
        int num1 = 50;
        int num2 = 60;

        // when
        int result = calculator.add(num1, num2);

        // then
        assertThat(result).isEqualTo(110);
    }

    class Calculator {
        public int add(int a, int b) {
            return a + b;
        }
    }
}
