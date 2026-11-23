import { describe, it, expect } from 'vitest'
import { letterOf, roleGroup, gradeText } from './api'

describe('letterOf (note /20 vers lettre A-F)', () => {
  it('attribue A dès 16', () => { expect(letterOf(16)).toBe('A'); expect(letterOf(20)).toBe('A') })
  it('attribue B entre 14 et 16', () => { expect(letterOf(15.99)).toBe('B'); expect(letterOf(14)).toBe('B') })
  it('attribue C entre 12 et 14', () => { expect(letterOf(13.5)).toBe('C'); expect(letterOf(12)).toBe('C') })
  it('attribue D entre 10 et 12', () => { expect(letterOf(11.99)).toBe('D'); expect(letterOf(10)).toBe('D') })
  it('attribue F sous 10', () => { expect(letterOf(9.99)).toBe('F'); expect(letterOf(0)).toBe('F') })
})

describe('roleGroup', () => {
  it('route chaque rôle vers son espace', () => {
    expect(roleGroup('STUDENT')).toBe('student')
    expect(roleGroup('TEACHER')).toBe('teacher')
    expect(roleGroup('ADMIN')).toBe('admin')
    expect(roleGroup('SUPER_ADMIN')).toBe('admin')
  })
})

describe('gradeText', () => {
  it('affiche la lettre avec son libellé', () => { expect(gradeText('A')).toBe('A – Très bien') })
  it('affiche un tiret si la note est absente', () => { expect(gradeText(null)).toBe('—') })
})
