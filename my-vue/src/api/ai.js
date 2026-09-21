import request from './index'

/**
 * Agent 对话（带工具调用）
 */
export function chatWithAgent(messages, userType, userId) {
    return request({
        url: '/ai/agent/chat',
        method: 'post',
        data: { messages, userType, userId },
        timeout: 120000  // Agent 多轮调用，超时设长一点
    })
}
