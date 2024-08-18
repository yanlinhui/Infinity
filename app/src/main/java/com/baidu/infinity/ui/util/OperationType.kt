package com.baidu.infinity.ui.util

/**
 * 图标的功能类型
 */
enum class OperationType {
    NONE, //不做任何操作

    DRAW_MENU,
    DRAW_MOVE,
    DRAW_ERASER,
    DRAW_LINE_ARROW,
    DRAW_CIRCLE,
    DRAW_RECTANGLE,
    DRAW_TRIANGLE,
    DRAW_TEXT,
    DRAW_LINE,
    DRAW_BEZEL,
    DRAW_LOCATION,
    DRAW_CURVE,
    DRAW_BRUSH,

    MENU_SAVE,
    MENU_DOWNLOAD,
    MENU_SHARE,
    MENU_PICTURE,
    MENU_ACCOUNT,
    MENU_LAYER,

    OPERATION_UNDO,
    OPERATION_DELETE,
    OPERATION_PENCIL,
    OPERATION_PALETTE
}