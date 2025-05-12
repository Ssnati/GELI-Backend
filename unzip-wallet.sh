#!/bin/sh

echo "Descomprimiendo wallet..."

if [ -z "$WALLET_B64" ]; then
  echo "⚠️  Variable WALLET_B64 no está definida. Saltando descompresión."
  exit 0
fi

mkdir -p "$WALLET_PATH"

# Escribe y decodifica el base64 a un archivo ZIP temporal
echo "$WALLET_B64" | base64 -d > "$WALLET_PATH/wallet.zip"

# Extrae el contenido del ZIP
unzip -o "$WALLET_PATH/wallet.zip" -d "$WALLET_PATH"

# Elimina el archivo zip temporal
rm "$WALLET_PATH/wallet.zip"

echo "✅ Wallet descomprimido en $WALLET_PATH"
