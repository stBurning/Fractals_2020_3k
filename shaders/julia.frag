#version 330 core

uniform vec2 u_resolution;
uniform vec2 u_offset;

out vec4 FragColor;

const int MAX_ITERATIONS = 256;

struct complex {
    float real;
    float imaginary;
};

int fractal(complex c, complex z) {
    for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {

        // z <- z^2 + c
        float real = z.real * z.real - z.imaginary * z.imaginary + c.real;
        float imaginary = 2.0 * z.real * z.imaginary + c.imaginary;

        z.real = real;
        z.imaginary = imaginary;

        if (z.real * z.real + z.imaginary * z.imaginary > 4.0) {
            return iteration;
        }
    }

    return 0;
}

int mandelbrot(vec2 coordinate) {
    complex c = complex(coordinate.x, coordinate.y);
    complex z = complex(0.0, 0.0);

    return fractal(c, z);
}

int julia(vec2 coordinate, vec2 offset) {
    complex c = complex(offset.x, offset.y);
    complex z = complex(coordinate.x, coordinate.y);

    return fractal(c, z);
}

vec2 fragCoordToXY(vec2 fragCoord) {
    vec2 relativePosition = fragCoord.xy / u_resolution.xy;
    float aspectRatio = u_resolution.x / u_resolution.y;

    vec2 cartesianPosition = (relativePosition - 0.5) * 4.0;
    cartesianPosition.x *= aspectRatio;

    return cartesianPosition;
}

void main() {
    vec2 coordinate    = fragCoordToXY(gl_FragCoord.xy);
    vec2 clickPosition = u_offset;

    int juliaValue = julia(coordinate, clickPosition);
    int mandelbrotValue = mandelbrot(coordinate);

    float clickPoint;
    if(length(clickPosition - coordinate) < 0.05){
        clickPoint = 1.0;
    } else {
        clickPoint = 0.0;
    }

    float juliaColor      = 5.0 * float(juliaValue) / float(MAX_ITERATIONS);
    float mandelbrotColor = 5.0 * float(mandelbrotValue) / float(MAX_ITERATIONS);

    float color = mandelbrotColor + juliaColor;
    FragColor = vec4(color, color, color + clickPoint, 1.0);
}
