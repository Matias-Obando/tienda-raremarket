export type Talla = 'XS' | 'S' | 'M' | 'L' | 'XL'
export type Estado = 'Nuevo' | 'Como nuevo' | 'Usado'
export type Genero = 'Hombre' | 'Mujer' | 'Unisex'
export type Categoria = string

export type Item = {
  id: string
  sellerId?: string
  titulo: string
  descripcion: string
  precioEur: number
  categoria: Categoria
  subcategoria?: string
  genero?: Genero
  marca: string
  talla: Talla
  estado: Estado
  imagen: string
  images?: string[] 
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
    genero: 'Hombre',
    talla: 'M',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa1/800/800',
    images: [
      'https://picsum.photos/seed/ropa1a/1200/900',
      'https://picsum.photos/seed/ropa1b/1200/900',
      'https://picsum.photos/seed/ropa1c/1200/900'
    ],
    creadoHace: 'hace 2 horas',
  },
  {
    id: '2',
    titulo: 'Camiseta básica blanca',
    descripcion: 'Algodón, corte regular. Ideal para diario.',
    precioEur: 8,
    categoria: 'Camisetas',
    marca: 'Zara',
    genero: 'Mujer',
    talla: 'S',
    estado: 'Como nuevo',
    imagen: 'https://picsum.photos/seed/ropa2/800/800',
    images: [
      'https://picsum.photos/seed/ropa2a/1200/900',
      'https://picsum.photos/seed/ropa2b/1200/900',
      'https://picsum.photos/seed/ropa2c/1200/900'
    ],
    creadoHace: 'hace 1 día',
  },
  {
    id: '3',
    titulo: 'Chaqueta vaquera oversize',
    descripcion: 'Vaquera clásica, estilo oversize.',
    precioEur: 30,
    categoria: 'Chaquetas',
    marca: 'Pull&Bear',
    genero: 'Unisex',
    talla: 'L',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa3/800/800',
    images: [
      'https://picsum.photos/seed/ropa3a/1200/900',
      'https://picsum.photos/seed/ropa3b/1200/900',
      'https://picsum.photos/seed/ropa3c/1200/900'
    ],
    creadoHace: 'hace 3 días',
  },
  {
    id: '4',
    titulo: 'Pantalón cargo negro',
    descripcion: 'Cargo con bolsillos. Muy cómodo.',
    precioEur: 18,
    categoria: 'Pantalones',
    marca: 'Bershka',
    genero: 'Hombre',
    talla: 'M',
    estado: 'Como nuevo',
    imagen: 'https://picsum.photos/seed/ropa4/800/800',
    images: [
      'https://picsum.photos/seed/ropa4a/1200/900',
      'https://picsum.photos/seed/ropa4b/1200/900',
      'https://picsum.photos/seed/ropa4c/1200/900'
    ],
    creadoHace: 'hace 5 horas',
  },
  {
    id: '5',
    titulo: 'Zapatillas Adidas',
    descripcion: 'Usadas pero bien cuidadas.',
    precioEur: 22,
    categoria: 'Zapatillas',
    marca: 'Adidas',
    genero: 'Unisex',
    talla: 'L',
    estado: 'Usado',
    imagen: 'https://picsum.photos/seed/ropa5/800/800',
    images: [
      'https://picsum.photos/seed/ropa5a/1200/900',
      'https://picsum.photos/seed/ropa5b/1200/900',
      'https://picsum.photos/seed/ropa5c/1200/900'
    ],
    creadoHace: 'hace 4 días',
  },
]