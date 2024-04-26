from abc import ABC, abstractmethod
class Dataset(ABC):
    """
    Abstract base class representing a dataset
    """
    _name: str


    @property
    def name(self) -> str:
        return self._name

    @name.setter
    def name(self, value) -> None:
        self._name = value


    @abstractmethod
    def load(self) -> None:
        """Implemented in a mixin."""
        pass

    @abstractmethod
    def preprocess(self) -> None:
        pass

    @abstractmethod
    def feature_engineer(self) -> None:
        pass

    @abstractmethod
    def save(self) -> None:
        """Implemented in a mixin."""
        pass
