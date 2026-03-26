import { describe, expect, it } from 'vitest'
import { sirmemTokens } from './design-tokens'

describe('Sirmem design tokens', () => {
  it('defines reusable visual primitives for brand, layout, and placeholder media', () => {
    expect(sirmemTokens.color.brand).toBe('#ff5a5f')
    expect(sirmemTokens.color.textMain).toBe('#18212f')
    expect(sirmemTokens.radius.pill).toBe('999px')
    expect(sirmemTokens.shadow.card).toContain('rgba(15, 23, 42')
    expect(sirmemTokens.space[4]).toBe('16px')
    expect(sirmemTokens.layout.maxWidth).toBe('480px')
    expect(sirmemTokens.media.gradient).toContain('linear-gradient')
  })
})
