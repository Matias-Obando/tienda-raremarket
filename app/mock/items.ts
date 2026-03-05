export type Talla = 'XS' | 'S' | 'M' | 'L' | 'XL'
export type Estado = 'Nuevo' | 'Como nuevo' | 'Usado'
export type Categoria =
  | 'Camisetas'
  | 'Sudaderas'
  | 'Pantalones'
  | 'Chaquetas'
  | 'Zapatillas'
  | 'Accesorios'

export type Item = {
  id: string
  titulo: string
  descripcion: string
  precioEur: number
  categoria: Categoria
  marca: string
  talla: Talla
  estado: Estado
  imagen: string
  creadoHace: string
}

export const mockItems: Item[] = [
  {
    id: '1',
    titulo: 'Sudadera Nike vintage',
    descripcion: 'Sudadera cómoda, sin rotos. Pequeño desgaste normal.',
    precioEur: 25,
    categoria: 'Sudaderas',
    marca: 'Nike',
    talla: 'M',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa1/800/800',
    creadoHace: 'hace 2 horas',
  },
  {
    id: '2',
    titulo: 'Camiseta básica blanca',
    descripcion: 'Algodón, corte regular. Ideal para diario.',
    precioEur: 8,
    categoria: 'Camisetas',
    marca: 'Zara',
    talla: 'S',
    estado: 'Como nuevo',
    imagen: 'https://picsum.photos/seed/ropa2/800/800',
    creadoHace: 'hace 1 día',
  },
  {
    id: '3',
    titulo: 'Chaqueta vaquera oversize',
    descripcion: 'Vaquera clásica, estilo oversize.',
    precioEur: 30,
    categoria: 'Chaquetas',
    marca: 'Pull&Bear',
    talla: 'L',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa3/800/800',
    creadoHace: 'hace 3 días',
  },
  {
    id: '4',
    titulo: 'Pantalón cargo negro',
    descripcion: 'Cargo con bolsillos. Muy cómodo.',
    precioEur: 18,
    categoria: 'Pantalones',
    marca: 'Bershka',
    talla: 'M',
    estado: 'Como nuevo',
    imagen: 'https://picsum.photos/seed/ropa4/800/800',
    creadoHace: 'hace 5 horas',
  },
  {
    id: '5',
    titulo: 'Zapatillas Adidas',
    descripcion: 'Usadas pero bien cuidadas.',
    precioEur: 22,
    categoria: 'Zapatillas',
    marca: 'Adidas',
    talla: 'L',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa5/800/800',
    creadoHace: 'hace 4 días',
  },
]