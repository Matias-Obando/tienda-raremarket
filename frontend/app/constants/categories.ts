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
