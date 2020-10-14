// 定义所有浮点类型数据的默认精度，这里是选择中等精确度
precision mediump float;

// 让每个顶点都使用同一个值
uniform vec4 u_Color;

void main()
{
    // 把在 uniform 里定义的颜色复制到特殊的输出变量-- gl_FragColor 里
    gl_FragColor = u_Color;
}