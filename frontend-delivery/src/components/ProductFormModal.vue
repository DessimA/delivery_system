<template>
  <BaseModal :modelValue="modelValue" @update:modelValue="$emit('update:modelValue', $event)" :title="modalTitle">
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="nomeProduto">Nome do Produto</label>
        <BaseInput
          id="nomeProduto"
          v-model="form.nomeProduto"
          placeholder="Ex: Pizza Calabresa"
          :error="errors.nomeProduto"
        />
      </div>

      <div class="form-group">
        <label for="descricao">Descrição</label>
        <BaseInput
          id="descricao"
          v-model="form.descricao"
          type="textarea"
          placeholder="Uma breve descrição do produto"
          :error="errors.descricao"
        />
      </div>

      <div class="form-group">
        <label for="preco">Preço</label>
        <BaseInput
          id="preco"
          v-model.number="form.preco"
          type="number"
          step="0.01"
          placeholder="0.00"
          :error="errors.preco"
        />
      </div>

      <div class="form-group">
        <label for="caminhoImagem">URL da Imagem</label>
        <BaseInput
          id="caminhoImagem"
          v-model="form.caminhoImagem"
          placeholder="https://example.com/image.jpg"
          :error="errors.caminhoImagem"
        />
      </div>

      <div class="modal-footer">
        <BaseButton label="Cancelar" variant="secondary" @click="closeModal" />
        <BaseButton type="submit" :label="isEditing ? 'Salvar Alterações' : 'Adicionar Produto'" :loading="loading" />
      </div>
    </form>
  </BaseModal>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import BaseModal from '@/components/base/BaseModal.vue';
import BaseInput from '@/components/base/BaseInput.vue';
import BaseButton from '@/components/base/BaseButton.vue';
import { useNotifications } from '@/composables/useNotifications';

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  product: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  saving: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(['update:modelValue', 'save']);
const { addNotification } = useNotifications();

const defaultForm = {
  idProduto: null,
  nomeProduto: '',
  descricao: '',
  preco: 0,
  caminhoImagem: '',
};

const form = ref({ ...defaultForm });
const errors = ref({});

const isEditing = computed(() => !!form.value.idProduto);
const modalTitle = computed(() => (isEditing.value ? 'Editar Produto' : 'Adicionar Novo Produto'));

watch(() => props.product, (newProduct) => {
  if (newProduct) {
    form.value = { ...newProduct };
  } else {
    form.value = { ...defaultForm };
  }
  errors.value = {}; // Clear errors on product change
}, { immediate: true });

watch(() => props.modelValue, (newVal) => {
  if (!newVal) {
    // Reset form and errors when modal closes
    form.value = { ...defaultForm };
    errors.value = {};
  }
});

const validateForm = () => {
  let isValid = true;
  errors.value = {};

  if (!form.value.nomeProduto) {
    errors.value.nomeProduto = 'Nome do produto é obrigatório.';
    isValid = false;
  }
  if (!form.value.descricao) {
    errors.value.descricao = 'Descrição é obrigatória.';
    isValid = false;
  }
  if (form.value.preco <= 0) {
    errors.value.preco = 'Preço deve ser maior que zero.';
    isValid = false;
  }
  // Basic URL validation for image path
  if (form.value.caminhoImagem && !/^https?:\/\/.+\.(png|jpg|jpeg|gif|svg)$/.test(form.value.caminhoImagem)) {
    errors.value.caminhoImagem = 'URL da imagem inválida. Deve ser uma URL válida de imagem (png, jpg, jpeg, gif, svg).';
    isValid = false;
  }

  return isValid;
};

const handleSubmit = () => {
  if (validateForm()) {
    emit('save', { ...form.value });
  } else {
    addNotification({ type: 'error', message: 'Por favor, corrija os erros no formulário.' });
  }
};

const closeModal = () => {
  emit('update:modelValue', false);
};
</script>

<style lang="scss" scoped>
.form-group {
  margin-bottom: var(--spacing-md);

  label {
    display: block;
    margin-bottom: var(--spacing-sm);
    font-weight: var(--font-weight-medium);
    color: var(--color-text-dark);
  }

  .base-input { // Targeting the BaseInput component's root element
    width: 100%;
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
  margin-top: var(--spacing-lg);
}
</style>