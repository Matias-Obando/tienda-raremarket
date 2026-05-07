export type CategoryTreeNode = {
  key: string
  label: string
  subcategories: string[]
}

export const CATEGORY_TREE: CategoryTreeNode[] = [
  { key: 'abrigos', label: 'Abrigos', subcategories: ['Plumíferos', 'Trench', 'Lana', 'Piel sintética'] },
  { key: 'chaquetas', label: 'Chaquetas', subcategories: ['Denim', 'Cuero', 'Bómber', 'Blazer'] },
  { key: 'jerseis', label: 'Jerséis & Sudaderas', subcategories: ['Jersey', 'Sudadera', 'Cárdigan', 'Hoodie'] },
  { key: 'vestidos', label: 'Vestidos', subcategories: ['Falda', 'Vestido corto', 'Vestido midi', 'Vestido largo', 'Mono'] },
  { key: 'camisas', label: 'Camisas & Camisetas', subcategories: ['Camiseta', 'Camisa', 'Top', 'Polo'] },
  { key: 'pantalones', label: 'Pantalones', subcategories: ['Chino', 'Cargo', 'Leggings', 'Short'] },
  { key: 'vaqueros', label: 'Vaqueros', subcategories: ['Slim', 'Mom fit', 'Straight', 'Flare'] },
  { key: 'calzado', label: 'Calzado', subcategories: ['Zapatillas', 'Botas', 'Sandalias', 'Tacones'] },
  { key: 'bolsos', label: 'Bolsos', subcategories: ['Bandolera', 'Tote', 'Mochila', 'Clutch'] }
]

export const CATEGORY_KEY_TO_LABEL: Record<string, string> = CATEGORY_TREE.reduce((acc, node) => {
  acc[node.key] = node.label
  return acc
}, {} as Record<string, string>)

export function normalizeCategoryText(value: string | null | undefined): string {
  return (value ?? '')
    .trim()
    .toLowerCase()
    .normalize('NFD')
    .replace(/\p{Diacritic}/gu, '')
}

export function resolveCategoryLabel(value: string | null | undefined): string | null {
  const normalized = normalizeCategoryText(value)
  if (!normalized) {
    return null
  }

  const byKey = CATEGORY_TREE.find((node) => normalizeCategoryText(node.key) === normalized)
  if (byKey) {
    return byKey.label
  }

  const byLabel = CATEGORY_TREE.find((node) => normalizeCategoryText(node.label) === normalized)
  const fallback = (value ?? '').trim()
  return byLabel?.label ?? (fallback || null)
}

export function resolveCategoryKey(value: string | null | undefined): string | null {
  const normalized = normalizeCategoryText(value)
  if (!normalized) {
    return null
  }

  const byKey = CATEGORY_TREE.find((node) => normalizeCategoryText(node.key) === normalized)
  if (byKey) {
    return byKey.key
  }

  const byLabel = CATEGORY_TREE.find((node) => normalizeCategoryText(node.label) === normalized)
  return byLabel?.key ?? null
}

export function getSubcategoriesByKey(categoryKey: string | null | undefined): string[] {
  if (!categoryKey) {
    return []
  }

  const node = CATEGORY_TREE.find((entry) => entry.key === categoryKey)
  return node?.subcategories ?? []
}

export function composeCategoriaLabel(categoryKey: string, subcategory?: string | null): string {
  const parent = CATEGORY_KEY_TO_LABEL[categoryKey] ?? categoryKey
  const trimmedSubcategory = (subcategory ?? '').trim()
  return trimmedSubcategory ? `${parent} > ${trimmedSubcategory}` : parent
}

export function parseCategoriaLabel(categoria: string | null | undefined): { parent: string; subcategory: string } {
  const normalized = (categoria ?? '').trim()
  if (!normalized) {
    return { parent: '', subcategory: '' }
  }

  const [parent, ...rest] = normalized.split('>')
  return {
    parent: parent.trim(),
    subcategory: rest.join('>').trim()
  }
}

export function matchesCategoryKey(categoria: string | null | undefined, categoryKey: string): boolean {
  const expectedParent = CATEGORY_KEY_TO_LABEL[categoryKey]
  if (!expectedParent) {
    return false
  }

  const { parent } = parseCategoriaLabel(categoria)
  return parent.toLowerCase() === expectedParent.toLowerCase()
}

export function matchesCategorySelection(categoria: string | null | undefined, selection: string | null | undefined): boolean {
  const normalizedSelection = normalizeCategoryText(selection)
  if (!normalizedSelection) {
    return false
  }

  const parsed = parseCategoriaLabel(categoria)
  const itemParent = normalizeCategoryText(parsed.parent)
  const itemCategory = normalizeCategoryText(categoria)
  const resolvedLabel = normalizeCategoryText(resolveCategoryLabel(selection))
  const resolvedKey = normalizeCategoryText(resolveCategoryKey(selection))

  return [itemParent, itemCategory].includes(normalizedSelection) ||
    (resolvedLabel ? [itemParent, itemCategory].includes(resolvedLabel) : false) ||
    (resolvedKey ? [itemParent, itemCategory].includes(resolvedKey) : false)
}

export function matchesSubcategorySelection(
  categoria: string | null | undefined,
  itemSubcategory: string | null | undefined,
  selection: string | null | undefined
): boolean {
  const normalizedSelection = normalizeCategoryText(selection)
  if (!normalizedSelection) {
    return false
  }

  const parsed = parseCategoriaLabel(categoria)
  const itemSub = normalizeCategoryText(itemSubcategory)
  const parsedSub = normalizeCategoryText(parsed.subcategory)

  return [itemSub, parsedSub].includes(normalizedSelection)
}
